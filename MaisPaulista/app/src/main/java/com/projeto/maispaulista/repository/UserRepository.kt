package com.projeto.maispaulista.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.projeto.maispaulista.model.User

class UserRepository(private val auth: FirebaseAuth, private val db: FirebaseFirestore) {
    fun createUser(
        email: String,
        password: String,
        user: User,
        callback: (Boolean, String?) -> Unit
    ) {

        //implementar logica de verificação de e-mail
        
        //salvar dados no authentiication
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid

                if (userId != null) {
                    // Salvar dados no Firestore
                    db.collection("users").document(userId).set(user)
                        .addOnSuccessListener {
                            Log.d("Cadastro", "Usuário salvo com sucesso no Firestore")
                            callback(true, null)
                        }
                        .addOnFailureListener { exception ->
                            Log.e("Cadastro", "Erro ao salvar dados no Firestore: ${exception.message}")
                            callback(false, "Erro ao salvar dados: ${exception.message}")
                        }
                } else {
                    Log.e("Cadastro", "Erro: ID do usuário não encontrado")
                    callback(false, "Erro: ID do usuário não encontrado.")
                }
            } else {
                val errorMessage = task.exception?.localizedMessage ?: "Erro desconhecido."
                Log.e("UserRepository", "Erro ao criar usuário: $errorMessage")
                callback(false, "Erro ao criar usuário: $errorMessage")
            }
        }
    }
}