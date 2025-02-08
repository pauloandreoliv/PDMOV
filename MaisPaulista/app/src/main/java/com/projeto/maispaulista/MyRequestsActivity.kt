package com.projeto.maispaulista

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.projeto.maispaulista.adapter.Variaveis
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

        fetchRequests()
    }

    private fun fetchRequests() {
        lifecycleScope.launch {
            val requests = requestService.getUserRequests()
            for (request in requests) {
                addRequestTextView(request)
            }
        }
    }

    private fun addRequestTextView(request: RequestModel) {
        val textView = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(48, 20, 48, 0)  // Ajuste as margens conforme necessário
            }
            text = "Solicitação: ${request.id}\nTipo: ${request.tipoItem}\nData: ${request.data}\nStatus: ${request.status}"
            setPadding(16, 16, 16, 16)
            setTextColor(resources.getColor(android.R.color.white))
            // Definir o background de acordo com o status usando drawable
            when (request.status) {
                "Pendente" -> setBackgroundResource(R.drawable.background_yellow)
                "Deferido" -> setBackgroundResource(R.drawable.background_green)
                "Indeferido" -> setBackgroundResource(R.drawable.background_red)
            }
        }
        requestsContainer.addView(textView)
    }
}

