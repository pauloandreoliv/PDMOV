package com.projeto.maispaulista.service

import com.projeto.maispaulista.model.RequestModel
import com.projeto.maispaulista.repository.RequestRepository
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class RequestService(
    private val repository: RequestRepository,
    private val userId: String
) {

    suspend fun addRequest(tipoItem: String, descricao: String, imagemNome: String, endereco: String): Boolean {
        val dataAtual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())


        val count = repository.countDocumentsInCollection("requests")
        val request = RequestModel(
            nunSolicitacao = count.toInt() + 1,
            userId = userId,
            tipoItem = tipoItem,
            descricao = descricao,
            imagemNome = imagemNome,
            status = "Pendente",
            data = dataAtual,
            endereco = endereco
        )
        return repository.addRequest(request)
    }

    suspend fun getUserRequests(): List<RequestModel> {
        return repository.getRequestsByUser(userId)
    }

    suspend fun getRequestById(id: String): RequestModel? {
        return repository.getRequestById(id)
    }
}