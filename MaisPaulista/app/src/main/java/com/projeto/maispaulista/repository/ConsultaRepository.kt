package com.projeto.maispaulista.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.projeto.maispaulista.model.Consulta
import kotlinx.coroutines.tasks.await

class ConsultaRepository(private val db: FirebaseFirestore) {

    suspend fun getConsultas(): List<Consulta> {
        return try {
            val snapshot = db.collection("consultas").get().await()
            snapshot.documents.map { it.toObject(Consulta::class.java)!! }
        } catch (e: Exception) {
            emptyList()
        }
    }
}