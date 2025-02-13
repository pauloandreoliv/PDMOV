package com.projeto.maispaulista.service

import com.projeto.maispaulista.model.Agendamento
import com.projeto.maispaulista.model.Consulta
import com.projeto.maispaulista.repository.ConsultaRepository
import com.projeto.maispaulista.utils.ConsultaUtils



class ConsultaService(private val repository: ConsultaRepository, private val utils: ConsultaUtils) {

    suspend fun fetchConsultasByEspecialidade(especialidade: String): List<Consulta> {
        return repository.getConsultasByEspecialidade(especialidade)
    }

    suspend fun fetchConsultasAgendadasByUid(uid: String, status: String): List<Agendamento> {
        return repository.getConsultasAgendadasByUid(uid, status)
    }

    suspend fun agendarConsulta(consulta: Consulta, uid: String) {
        repository.agendarConsulta(consulta, uid)
        repository.atualizarDisponibilidadeConsulta(consulta.id)
    }

    suspend fun cancelarConsulta(consultaId: String, agendamentoId: String) {
        repository.atualizarConsultaDisponibilidade(consultaId, true)
        repository.removerConsultaAgendada(agendamentoId)
    }


}

