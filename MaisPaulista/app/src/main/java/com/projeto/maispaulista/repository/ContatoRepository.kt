package com.projeto.maispaulista.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.projeto.maispaulista.model.Contato

class ContatoRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("contatos_prefeitura")

    fun getContatos(callback: (List<Contato>) -> Unit) {
        collection.get().addOnSuccessListener { result ->
            val contatos = result.map { document -> document.toObject(Contato::class.java) }
            callback(contatos)
        }.addOnFailureListener { exception ->
            Log.w("Firestore", "Erro ao obter documentos.", exception)
        }
    }
}
