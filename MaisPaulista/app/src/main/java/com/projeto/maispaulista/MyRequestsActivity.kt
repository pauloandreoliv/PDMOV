package com.projeto.maispaulista

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.projeto.maispaulista.utils.Variaveis
import com.projeto.maispaulista.model.RequestModel
import com.projeto.maispaulista.repository.RequestRepository
import com.projeto.maispaulista.service.RequestService
import kotlinx.coroutines.launch

class MyRequestsActivity : AppCompatActivity() {

    private lateinit var requestsContainer: LinearLayout
    private lateinit var requestService: RequestService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myrequests)

        requestsContainer = findViewById(R.id.requestsContainer)

        // Inicializar Firestore e Repositório
        val firestore = FirebaseFirestore.getInstance()
        val requestRepository = RequestRepository(firestore)
        val userId = Variaveis.uid ?: ""
        requestService = RequestService(requestRepository, userId)

        // Configurar o clique no botão de voltar
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

        fetchRequests()
        setupBottomNavigation()
    }

    private fun fetchRequests() {
        lifecycleScope.launch {
            val requests = requestService.getUserRequests()
            for (request in requests) {
                addRequestTextView(request)
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, PrincipalActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun addRequestTextView(request: RequestModel) {
        val textView = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(48, 20, 48, 0)  // Ajuste as margens conforme necessário
            }
            text = "Solicitação: ${request.nunSolicitacao}\nTipo: ${request.tipoItem}\nData: ${request.data}\nStatus: ${request.status}"
            setPadding(16, 16, 16, 16)
            setTextColor(resources.getColor(android.R.color.white))
            // Definir o background de acordo com o status usando drawable
            when (request.status) {
                "Pendente" -> setBackgroundResource(R.drawable.background_yellow)
                "Deferido" -> setBackgroundResource(R.drawable.background_green)
                "Indeferido" -> setBackgroundResource(R.drawable.background_red)
            }
            // Adicionar OnClickListener para redirecionar para a tela de detalhes
            setOnClickListener {
                val intent = Intent(this@MyRequestsActivity, RequestDetailsActivity::class.java).apply {
                    putExtra("requestId", request.id)
                }
                startActivity(intent)
            }
        }
        requestsContainer.addView(textView)
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
                    startActivity(Intent(this, MainActivity::class.java))
                    Variaveis.uid = null
                    true
                }

                else -> false
            }
        }
    }
}


