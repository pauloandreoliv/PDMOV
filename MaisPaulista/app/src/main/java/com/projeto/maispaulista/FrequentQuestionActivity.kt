package com.projeto.maispaulista


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.projeto.maispaulista.model.Perguntas
import com.projeto.maispaulista.service.PerguntasService
import com.projeto.maispaulista.utils.AuthUtils
import com.projeto.maispaulista.utils.NetworkUtils
import com.projeto.maispaulista.utils.Variaveis


class FrequentQuestionActivity : AppCompatActivity() {

    private lateinit var containerLayout: LinearLayout
    private val perguntasService = PerguntasService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frequent_questions)
        AuthUtils.checkAuthentication(this)

        containerLayout = findViewById(R.id.containerLayout)


        // Configurar a cor de status bar
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.white)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        loadPerguntas()
        setupBottomNavigation()

        val backArrow = findViewById<ImageView>(R.id.backArrow)
        backArrow.setOnClickListener {
            val intent = Intent(this, PrincipalActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    // Carrega os dados das perguntas
    private fun loadPerguntas() {
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
        perguntasService.getPerguntas { perguntas ->
            perguntas.forEach { pergunta ->
                addPerguntaView(pergunta)
            }
        }
    }

    // Configuração do botão de voltar

    override fun onBackPressed() {
        val intent = Intent(this, PrincipalActivity::class.java)
        startActivity(intent)
        finish()
    }


    // Adiciona a pergunta na tela
    private fun addPerguntaView(pergunta: Perguntas) {
        val perguntaLayout = LayoutInflater.from(this).inflate(R.layout.item_pergunta, containerLayout, false)

        val typeLabel = perguntaLayout.findViewById<TextView>(R.id.typeLabel)
        val typeLabel1 = perguntaLayout.findViewById<TextView>(R.id.typeLabel1)

        typeLabel.text = pergunta.pergunta
        typeLabel1.text = pergunta.resposta

        containerLayout.addView(perguntaLayout)
    }

    // Barra de navegação
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
