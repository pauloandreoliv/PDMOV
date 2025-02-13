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
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.projeto.maispaulista.utils.Variaveis
import kotlinx.coroutines.launch
import com.projeto.maispaulista.repository.ConsultaRepository
import com.projeto.maispaulista.service.ConsultaService
import com.projeto.maispaulista.utils.ConsultaUtils
import com.projeto.maispaulista.utils.NotificationHelper

class MyConsultationsActivity : AppCompatActivity() {

    private lateinit var consultaService: ConsultaService
    private lateinit var spinnerStatus: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_consultations)

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
                val status = parent?.getItemAtPosition(position).toString()
                fetchAndDisplayConsultasAgendadas(status)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, PrincipalActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun fetchAndDisplayConsultasAgendadas(status: String) {
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
}
