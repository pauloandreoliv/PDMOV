package com.projeto.maispaulista


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.projeto.maispaulista.model.Perguntas
import com.projeto.maispaulista.service.PerguntasService


class FrequentQuestionActivity : AppCompatActivity() {

    private lateinit var containerLayout: LinearLayout
    private val perguntasService = PerguntasService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frequent_questions)

        containerLayout = findViewById(R.id.containerLayout)


        // Configurar a cor de status bar
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.white)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        loadPerguntas()
    }

    private fun loadPerguntas() {
        perguntasService.getPerguntas { perguntas ->
            perguntas.forEach { pergunta ->
                addPerguntaView(pergunta)
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, PrincipalActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun addPerguntaView(pergunta: Perguntas) {
        val perguntaLayout = LayoutInflater.from(this).inflate(R.layout.item_pergunta, containerLayout, false)

        val typeLabel = perguntaLayout.findViewById<TextView>(R.id.typeLabel)
        val typeLabel1 = perguntaLayout.findViewById<TextView>(R.id.typeLabel1)

        typeLabel.text = pergunta.pergunta
        typeLabel1.text = pergunta.resposta

        containerLayout.addView(perguntaLayout)
    }
}
