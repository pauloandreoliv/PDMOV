package com.projeto.maispaulista.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.projeto.maispaulista.model.User

class UserRepository(private val auth: FirebaseAuth, private val db: FirebaseFirestore) {
    fun createUser(email: String, password: String, user: User, callback: (Boolean, String?) -> Unit) {
        //salvar dados no authentiication
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    // Salvar dados no Firestore
                    db.collection("users").document(userId).set(user)
                        .addOnSuccessListener {
                            callback(true, null) // Sucesso
                        }
                        .addOnFailureListener { exception ->
                            callback(false, "Erro ao salvar dados: ${exception.message}")
                        }
                } else {
                    callback(false, "Erro: ID do usuário não encontrado.")
                }
            } else {
                callback(false, "Erro ao criar usuário: ${task.exception?.message}")
            }
        }
    }

    fun loginUser(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, null) // Login bem-sucedido
            } else {
                callback(false, "Erro ao fazer login: ${task.exception?.message}")
            }
        }
    }


}