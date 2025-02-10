package com.projeto.maispaulista.service

import com.projeto.maispaulista.model.Consulta
import com.projeto.maispaulista.repository.ConsultaRepository

class ConsultaService(private val repository: ConsultaRepository) {

    suspend fun fetchConsultasByEspecialidade(especialidade: String): List<Consulta> {
        return repository.getConsultasByEspecialidade(especialidade)
    }

    suspend fun agendarConsulta(consulta: Consulta, uid: String) {
        repository.agendarConsulta(consulta, uid)
        repository.atualizarDisponibilidadeConsulta(consulta.id)
    }
}

