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
import com.projeto.maispaulista.repository.ConsultaRepository
import com.projeto.maispaulista.service.ConsultaService
import com.projeto.maispaulista.utils.AuthUtils
import com.projeto.maispaulista.utils.ConsultaUtils
import com.projeto.maispaulista.utils.NetworkUtils
import com.projeto.maispaulista.utils.NotificationHelper
import kotlinx.coroutines.launch


class ScheduleConsultationsActivity : AppCompatActivity() {

    private lateinit var consultaService: ConsultaService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_consultations)
        AuthUtils.checkAuthentication(this)

        val currentUser = Variaveis.uid
        if (currentUser == null) {
            Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
            FirebaseAuth.getInstance().signOut()
            Variaveis.uid = null
            startActivity(Intent(this, MainActivity::class.java))
        }


        val db = FirebaseFirestore.getInstance()
        val consultaRepository = ConsultaRepository(db)

        val notificationHelper = NotificationHelper(this)

        val consultaUtils = ConsultaUtils(db, notificationHelper)

        consultaService = ConsultaService(consultaRepository, consultaUtils)
        lifecycleScope.launch {
            consultaUtils.updateConsultaStatusIfNeeded()
            fetchAndDisplayConsultas("Escolha a Especialidade")
        }

        val backArrow = findViewById<ImageView>(R.id.backArrow)
        backArrow.setOnClickListener {
            val intent = Intent(this, PrincipalActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Configurar a cor de status bar
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.white)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        // Configurar o Spinner
        val spinnerEspecialidade: Spinner = findViewById(R.id.spinnerEspecialidade)
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.especialidades_array,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEspecialidade.adapter = adapter

        // Configurar o listener do Spinner
        spinnerEspecialidade.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (!NetworkUtils.isNetworkAvailable(this@ScheduleConsultationsActivity)) {
                    NetworkUtils.showNoNetworkDialog(this@ScheduleConsultationsActivity)
                    return
                }


                val especialidade = parent.getItemAtPosition(position).toString()

                // Atualizar o status das consultas antes de buscar e exibir as consultas
                lifecycleScope.launch {
                    consultaUtils.updateConsultaStatusIfNeeded()
                    fetchAndDisplayConsultas(especialidade)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Nenhuma ação necessária quando nada for selecionado
            }
        }

        setupBottomNavigation()
    }


    // Voltar à tela principal ao clicar no botão de voltar
    override fun onBackPressed() {
        val intent = Intent(this, PrincipalActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Função para exibir um AlertDialog
    private fun showAlertDialog(context: Context, title: String, message: String, especialidade: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            // Atualizar consultas após o diálogo ser fechado
            fetchAndDisplayConsultas(especialidade)
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


    // Função para buscar e exibir as consultas
    private fun fetchAndDisplayConsultas(especialidade: String) {
        lifecycleScope.launch {
            try {
                val consultas = consultaService.fetchConsultasByEspecialidade(especialidade)
                val container = findViewById<LinearLayout>(R.id.consultaItemsContainer)
                container.removeAllViews() // Limpar o contêiner antes de adicionar novas consultas

                if (consultas.isEmpty()) {
                    Log.d("FirestoreData", "Nenhuma consulta encontrada.")
                }

                consultas.forEach { consulta ->
                    // Verifica se a consulta está disponível (status booleano)
                    val isAvailable = consulta.disponivel // 'disponivel' é o campo booleano

                    if (isAvailable) {  // Apenas exibe consultas que estão disponíveis
                        val inflater = LayoutInflater.from(this@ScheduleConsultationsActivity)
                        val view = inflater.inflate(R.layout.item_consulta_agendar, container, false)

                        val textView = view.findViewById<TextView>(R.id.typeLabel)
                        val button = view.findViewById<Button>(R.id.buttonAgendar)

                        textView.text =
                            "${consulta.especialidade} \nDr.(a) ${consulta.doutor} \n${consulta.local} \n${consulta.data} às ${consulta.hora}"

                        button.setOnClickListener {

                            if (!NetworkUtils.isNetworkAvailable(this@ScheduleConsultationsActivity)) {
                                NetworkUtils.showNoNetworkDialog(this@ScheduleConsultationsActivity)
                                return@setOnClickListener
                            }
                            lifecycleScope.launch {
                                try {
                                    consultaService.agendarConsulta(consulta, Variaveis.uid!!)
                                    showAlertDialog(
                                        this@ScheduleConsultationsActivity,
                                        "Agendamento Confirmado",
                                        "Sua consulta foi agendada com sucesso!",
                                        especialidade
                                    )
                                    Log.d("FirestoreData", "Consulta agendada com sucesso!")
                                } catch (e: Exception) {
                                    Log.e("FirestoreError", "Erro ao agendar consulta: ${e.message}")
                                    showAlertDialog(
                                        this@ScheduleConsultationsActivity,
                                        "Erro",
                                        "Ocorreu um erro ao agendar a consulta. Tente novamente.",
                                        especialidade
                                    )
                                }
                            }
                        }

                        container.addView(view)
                    } else {
                        Log.d("FirestoreData", "Consulta indisponível: ${consulta.doutor}")
                    }
                }
            } catch (e: Exception) {
                Log.e("FirestoreError", "Erro ao buscar consultas: ${e.message}")
            }
        }
    }

    // Função para configurar a navegação inferior
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
