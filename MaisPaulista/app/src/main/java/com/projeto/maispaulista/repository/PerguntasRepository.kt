package com.projeto.maispaulista.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.projeto.maispaulista.model.Perguntas

class PerguntasRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("perguntas")

    // Função para obter todas as perguntas do Firestore
    fun getPerguntas(callback: (List<Perguntas>) -> Unit) {
        collection.get().addOnSuccessListener { result ->
            val perguntas = result.map { document -> document.toObject(Perguntas::class.java) }
            callback(perguntas)
        }.addOnFailureListener { exception ->
            Log.w("Firestore", "Erro ao obter documentos.", exception)
        }
    }
}
