package com.projeto.maispaulista

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.projeto.maispaulista.utils.Variaveis
import kotlinx.coroutines.launch
import com.projeto.maispaulista.repository.ConsultaRepository
import com.projeto.maispaulista.service.ConsultaService
import com.projeto.maispaulista.utils.AuthUtils
import com.projeto.maispaulista.utils.ConsultaUtils
import com.projeto.maispaulista.utils.NetworkUtils
import com.projeto.maispaulista.utils.NotificationHelper

class MyConsultationsActivity : AppCompatActivity() {

    private lateinit var consultaService: ConsultaService
    private lateinit var spinnerStatus: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_consultations)
        AuthUtils.checkAuthentication(this)

        val db = FirebaseFirestore.getInstance()
        val consultaRepository = ConsultaRepository(db)


        val notificationHelper = NotificationHelper(this)

        val consultaUtils = ConsultaUtils(db, notificationHelper)

        consultaService = ConsultaService(consultaRepository, consultaUtils)

        lifecycleScope.launch {
            consultaUtils.updateAgendamentoStatusIfNeeded()
        }

        // Configurar a cor de status bar
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.white)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        val backArrow = findViewById<ImageView>(R.id.backArrow)
        backArrow.setOnClickListener {
            val intent = Intent(this, PrincipalActivity::class.java)
            startActivity(intent)
            finish()
        }


        // Configurar o Spinner
        spinnerStatus = findViewById(R.id.spinnerStatus)
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.status_array,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = adapter

        spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (!NetworkUtils.isNetworkAvailable(this@MyConsultationsActivity)) {
                    NetworkUtils.showNoNetworkDialog(this@MyConsultationsActivity)
                    return
                }
                val status = parent?.getItemAtPosition(position).toString()
                fetchAndDisplayConsultasAgendadas(status)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        setupBottomNavigation()
    }

    override fun onBackPressed() {
        val intent = Intent(this, PrincipalActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun fetchAndDisplayConsultasAgendadas(status: String) {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            NetworkUtils.showNoNetworkDialog(this)
            return
        }
        val currentUser = Variaveis.uid
        if (currentUser == null) {
            Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
            FirebaseAuth.getInstance().signOut()
            Variaveis.uid = null
            return startActivity(Intent(this, MainActivity::class.java))
        }
        lifecycleScope.launch {
            try {
                val uid = Variaveis.uid!!
                val agendamentos = consultaService.fetchConsultasAgendadasByUid(uid, status)
                val container = findViewById<LinearLayout>(R.id.consultaItemsContainer)
                container.removeAllViews() // Limpar o contêiner antes de adicionar novas consultas

                if (agendamentos.isEmpty()) {
                    Log.d("FirestoreData", "Nenhuma consulta agendada encontrada.")
                }

                agendamentos.forEach { agendamento ->
                    val inflater = LayoutInflater.from(this@MyConsultationsActivity)
                    val view = inflater.inflate(R.layout.item_consulta_cancelar, container, false)

                    val textView = view.findViewById<TextView>(R.id.typeLabel)
                    val button = view.findViewById<Button>(R.id.buttoncancelar)

                    var text = "${agendamento.especialidade} \nDr.(a) ${agendamento.doutor} \n${agendamento.local} \n${agendamento.data} às ${agendamento.hora}"

                    if (agendamento.Concluida) {
                        text += "\nConsulta Finalizada"
                        button.visibility = View.GONE
                    } else {
                        button.setOnClickListener {
                            if (!NetworkUtils.isNetworkAvailable(this@MyConsultationsActivity)) {
                                NetworkUtils.showNoNetworkDialog(this@MyConsultationsActivity)
                                return@setOnClickListener
                            }

                            showAlertDialogupdate(
                                this@MyConsultationsActivity,
                                "Cancelar Consulta",
                                "Você tem certeza que deseja cancelar esta consulta?",
                                status,
                                agendamento.id,
                                view,
                                container
                            )
                        }
                    }

                    textView.text = text
                    container.addView(view)
                }
            } catch (e: Exception) {
                Log.e("FirestoreError", "Erro ao buscar consultas agendadas: ${e.message}")
            }
        }
    }

    private fun showAlertDialogupdate(
        context: Context,
        title: String,
        message: String,
        status: String,
        agendamentoId: String,
        view: View,
        container: LinearLayout
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            lifecycleScope.launch {
                try {
                    consultaService.cancelarConsulta(agendamentoId, agendamentoId)
                    container.removeView(view)
                    Log.d("FirestoreData", "Consulta cancelada com sucesso!")
                    // Atualizar a lista de consultas agendadas
                    fetchAndDisplayConsultasAgendadas(status)
                } catch (e: Exception) {
                    Log.e("FirestoreError", "Erro ao cancelar consulta: ${e.message}")
                }
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_settings -> {
                    Variaveis.currentActivity = this::class.java
                    startActivity(Intent(this, ConfigurationActivity::class.java))
                    true
                }

                R.id.navigation_home -> {
                    startActivity(Intent(this, PrincipalActivity::class.java))
                    true
                }

                R.id.navigation_back -> {
                    FirebaseAuth.getInstance().signOut() // Sai da autenticação
                    Variaveis.uid = null
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }
}
