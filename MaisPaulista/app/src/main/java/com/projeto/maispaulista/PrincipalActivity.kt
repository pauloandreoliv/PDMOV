package com.projeto.maispaulista

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.LinearLayout

class PrincipalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_principal)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // redirecionamentos

        val registrarSolicitacao: LinearLayout = findViewById(R.id.registrarSolicitacao)
        val acompanharSolicitaçao: LinearLayout = findViewById(R.id.acompanharSolicitaçao)
        val MyConsultas: LinearLayout = findViewById(R.id.MyConsultas)
        val agendarConsultas: LinearLayout = findViewById(R.id.agendarConsultas)
        val perguntasFrequentes: LinearLayout = findViewById(R.id.perguntasFrequentes)
        val contatos: LinearLayout = findViewById(R.id.contatos)

        //redirecionamentos

        registrarSolicitacao.setOnClickListener {
            val intent = Intent(this, RegisterResquestsActivity::class.java)
            startActivity(intent)
        }

        acompanharSolicitaçao.setOnClickListener {
            val intent = Intent(this, MyRequestsActivity::class.java)
            startActivity(intent)
        }

        MyConsultas.setOnClickListener {
            val intent = Intent(this, MyConsultationsActivity::class.java)
            startActivity(intent)
        }

        agendarConsultas.setOnClickListener {
            val intent = Intent(this, ScheduleConsultationsActivity::class.java)
            startActivity(intent)
        }

        perguntasFrequentes.setOnClickListener {
            val intent = Intent(this, FrequentQuestionActivity::class.java)
            startActivity(intent)
        }

        contatos.setOnClickListener {
            val intent = Intent(this, CityHallContactsActivity::class.java)
            startActivity(intent)
        }

        contatos.setOnClickListener {
            val intent = Intent(this, CityHallContactsActivity::class.java)
            startActivity(intent)
        }

        contatos.setOnClickListener {
            val intent = Intent(this, CityHallContactsActivity::class.java)
            startActivity(intent)
        }

        contatos.setOnClickListener {
            val intent = Intent(this, CityHallContactsActivity::class.java)
            startActivity(intent)
        }











    }
}