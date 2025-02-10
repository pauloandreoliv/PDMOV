package com.projeto.maispaulista

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.projeto.maispaulista.adapter.Variaveis
import com.projeto.maispaulista.model.Consulta
import com.projeto.maispaulista.repository.ConsultaRepository
import com.projeto.maispaulista.service.ConsultaService
import kotlinx.coroutines.launch


class ScheduleConsultationsActivity : AppCompatActivity() {

    private lateinit var consultaService: ConsultaService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_consultations)

        val db = FirebaseFirestore.getInstance()
        val consultaRepository = ConsultaRepository(db)
        consultaService = ConsultaService(consultaRepository)

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
                val especialidade = parent.getItemAtPosition(position).toString()
                fetchAndDisplayConsultas(especialidade)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Não fazer nada
            }
        }

        // Busca inicial com "Escolha a Especialidade"
        fetchAndDisplayConsultas("Escolha a Especialidade")
    }

    override fun onBackPressed() {
        val intent = Intent(this, PrincipalActivity::class.java)
        startActivity(intent)
        finish()
    }

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
                    val inflater = LayoutInflater.from(this@ScheduleConsultationsActivity)
                    val view = inflater.inflate(R.layout.consulta_item, container, false)

                    val textView = view.findViewById<TextView>(R.id.typeLabel)
                    val button = view.findViewById<Button>(R.id.buttonAgendar)

                    textView.text =
                        "${consulta.especialidade} \nDr.(a) ${consulta.doutor} \n${consulta.local} \n${consulta.data} às ${consulta.hora}"

                    button.setOnClickListener {
                        // Ação do botão, por exemplo, agendar consulta
                    }

                    container.addView(view)
                }
            } catch (e: Exception) {
                Log.e("FirestoreError", "Erro ao buscar consultas: ${e.message}")
            }
        }
    }
}
