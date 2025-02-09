package com.projeto.maispaulista.service

import com.projeto.maispaulista.model.Consulta
import com.projeto.maispaulista.repository.ConsultaRepository

class ConsultaService(private val repository: ConsultaRepository) {

    suspend fun fetchConsultas(): List<Consulta> {
        return repository.getConsultas()
    }
}