package com.projeto.maispaulista.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.projeto.maispaulista.model.Agendamento
import com.projeto.maispaulista.model.Consulta
import kotlinx.coroutines.tasks.await


class ConsultaRepository(private val db: FirebaseFirestore) {

    suspend fun getConsultasByEspecialidade(especialidade: String): List<Consulta> {
        val consultasRef = db.collection("consultas")
        val query = if (especialidade == "Escolha a Especialidade") {
            consultasRef.whereEqualTo("disponivel", true)
        } else {
            consultasRef.whereEqualTo("disponivel", true)
                .whereEqualTo("especialidade", especialidade)
        }

        val snapshot = query.get().await()
        Log.d("FirestoreData", "Total consultas (${especialidade}): ${snapshot.size()}")
        return snapshot.toObjects(Consulta::class.java).map { consulta ->
            consulta.copy(id = snapshot.documents.find { it.id == consulta.id }?.id ?: "")
        }
    }


    suspend fun agendarConsulta(consulta: Consulta, uid: String) {
        val consultasAgendadasRef = db.collection("consultas_agendadas")
        val agendamento = hashMapOf(
            "uid" to uid,
            "especialidade" to consulta.especialidade,
            "doutor" to consulta.doutor,
            "local" to consulta.local,
            "data" to consulta.data,
            "hora" to consulta.hora,
            "Concluida" to false,
            "id" to consulta.id
        )
        consultasAgendadasRef.document(consulta.id).set(agendamento).await()
        Log.d("FirestoreData", "Consulta agendada com sucesso!")
    }

    suspend fun atualizarDisponibilidadeConsulta(consultaId: String) {
        val consultaDocRef = db.collection("consultas").document(consultaId)
        consultaDocRef.update("disponivel", false).await()
        Log.d("FirestoreData", "Status da consulta atualizado para 'disponivel = false'")
    }

    suspend fun getConsultasAgendadasByUid(uid: String, status: String): List<Agendamento> {
        val agendamentosRef = db.collection("consultas_agendadas")
        val query = when (status) {
            "Consultas Concluídas" -> {
                agendamentosRef.whereEqualTo("uid", uid)
                    .whereEqualTo("Concluida", true)
            }
            "Consultas Agendadas" -> {
                agendamentosRef.whereEqualTo("uid", uid)
                    .whereEqualTo("Concluida", false)
            }
            else -> {
                agendamentosRef.whereEqualTo("uid", uid)
            }
        }
        val result = query.get().await()
        return result.toObjects(Agendamento::class.java)


    }
    suspend fun atualizarConsultaDisponibilidade(consultaId: String, disponivel: Boolean) {
        val consultaRef = db.collection("consultas").document(consultaId)
        consultaRef.update("disponivel", disponivel).await()
    }

    suspend fun removerConsultaAgendada(agendamentoId: String) {
        val agendamentoRef = db.collection("consultas_agendadas").document(agendamentoId)
        agendamentoRef.delete().await()
    }

}
