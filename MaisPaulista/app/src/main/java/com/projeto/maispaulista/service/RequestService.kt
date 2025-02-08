package com.projeto.maispaulista.service

import com.projeto.maispaulista.model.RequestModel
import com.projeto.maispaulista.repository.RequestRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class RequestService(
    private val repository: RequestRepository,
    private val userId: String
) {

    suspend fun addRequest(tipoItem: String, descricao: String, imagemNome: String): Boolean {
        val dataAtual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        val request = RequestModel(
            userId = userId,
            tipoItem = tipoItem,
            descricao = descricao,
            imagemNome = imagemNome,
            status = "Pendente",
            data = dataAtual  // Passando a data como String
        )
        return repository.addRequest(request)
    }

    suspend fun getUserRequests(): List<RequestModel> {
        return repository.getRequestsByUser(userId)
    }
}