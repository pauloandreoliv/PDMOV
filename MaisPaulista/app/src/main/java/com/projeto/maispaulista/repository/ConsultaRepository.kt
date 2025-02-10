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
        }

        val snapshot = query.get().await()
        Log.d("FirestoreData", "Documentos encontrados: ${snapshot.documents}")
        return snapshot.toObjects(Consulta::class.java)
    }
}
