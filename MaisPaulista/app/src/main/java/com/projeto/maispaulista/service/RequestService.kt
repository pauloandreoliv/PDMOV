package com.projeto.maispaulista.service

import com.projeto.maispaulista.model.RequestModel
import com.projeto.maispaulista.repository.RequestRepository

class RequestService(
    private val repository: RequestRepository,
    private val userId: String
) {

    suspend fun addRequest(tipoItem: String, descricao: String, imagemNome: String): Boolean {
        val request = RequestModel(userId = userId, tipoItem = tipoItem, descricao = descricao, imagemNome = imagemNome)
        return repository.addRequest(request)
    }

    suspend fun getUserRequests(): List<RequestModel> {
        return repository.getRequestsByUser(userId)
    }
}
