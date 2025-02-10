package com.projeto.maispaulista.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
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
                .whereEqualTo("status", "disponivel")
        }

        val snapshot = query.get().await()
        Log.d("FirestoreData", "Total consultas (${especialidade}): ${snapshot.size()}")
        val consultas = snapshot.documents.map { document ->
            document.toObject(Consulta::class.java)?.copy(id = document.id) ?: Consulta()
        }
        consultas.forEach { consulta ->
            Log.d("FirestoreData", "Consulta: $consulta")
        }
        return consultas
    }

    suspend fun agendarConsulta(consulta: Consulta, uid: String) {
        val consultasAgendadasRef = db.collection("consultas_agendadas")
        val agendamento = hashMapOf(
            "uid" to uid,
            "especialidade" to consulta.especialidade,
            "doutor" to consulta.doutor,
            "local" to consulta.local,
            "data" to consulta.data,
            "hora" to consulta.hora
        )
        consultasAgendadasRef.add(agendamento).await()
        Log.d("FirestoreData", "Consulta agendada com sucesso!")
    }

    suspend fun atualizarDisponibilidadeConsulta(consultaId: String) {
        val consultaDocRef = db.collection("consultas").document(consultaId)
        consultaDocRef.update("disponivel", false).await()
        Log.d("FirestoreData", "Status da consulta atualizado para 'disponivel = false'")
    }

}