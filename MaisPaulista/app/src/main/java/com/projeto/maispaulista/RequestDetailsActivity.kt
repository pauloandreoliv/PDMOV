package com.projeto.maispaulista

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.ImageView
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.projeto.maispaulista.adapter.Variaveis
import com.projeto.maispaulista.repository.RequestRepository
import com.projeto.maispaulista.service.RequestService
import kotlinx.coroutines.launch
import java.io.File

class RequestDetailsActivity : AppCompatActivity() {

    private lateinit var requestService: RequestService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_details)

        // Inicializar Firestore e Repositório
        val firestore = FirebaseFirestore.getInstance()
        val requestRepository = RequestRepository(firestore)
        val userId = Variaveis.uid ?: ""
        requestService = RequestService(requestRepository, userId)

        // Obter o ID da solicitação da intent
        val requestId = intent.getStringExtra("requestId")

        // Buscar e exibir os detalhes da solicitação
        requestId?.let {
            fetchRequestDetails(it)
        }


        // Configurar a cor de status bar
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.white)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        // Configurar o clique no botão de voltar
        val backArrow = findViewById<ImageView>(R.id.backArrow)
        backArrow.setOnClickListener {
            val intent = Intent(this, MyRequestsActivity::class.java)
            startActivity(intent)
            finish()
        }
        setupBottomNavigation()
    }

    private fun fetchRequestDetails(id: String) {
        lifecycleScope.launch {
            val request = requestService.getRequestById(id)
            request?.let {
                // Configurar os TextViews com os detalhes da solicitação
                findViewById<TextView>(R.id.titleTextView).text = "Solicitação\nN°${it.nunSolicitacao}"
                findViewById<TextView>(R.id.typeLabel).text = "Status: ${it.status}\n\nTipo: ${it.tipoItem}\n\nData: ${it.data}\n\nDescrição: ${it.descricao}\n\nEndereço: implementar \n\nImagem: ${it.imagemNome}"

                val imagePath = File(filesDir, "Banco_imagens_solicitacao/${it.imagemNome}")
                if (imagePath.exists()) {
                    val bitmap = BitmapFactory.decodeFile(imagePath.absolutePath)
                    findViewById<ImageView>(R.id.imageView).setImageBitmap(bitmap)
                }

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
                    startActivity(Intent(this, MainActivity::class.java))
                    Variaveis.uid = ""
                    true
                }

                else -> false
            }
        }
    }
}