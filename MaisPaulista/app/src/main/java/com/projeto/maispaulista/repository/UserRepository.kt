package com.projeto.maispaulista.repository

import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.projeto.maispaulista.model.User
import kotlinx.coroutines.tasks.await

class UserRepository(private val auth: FirebaseAuth, private val db: FirebaseFirestore ) {

    private val userCollection = db.collection("users")

    fun createUser(
        email: String,
        password: String,
        user: User,
        callback: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    db.collection("users").document(userId).set(user)
                        .addOnSuccessListener {
                            Log.d("Cadastro", "Usuário salvo com sucesso no Firestore")
                            callback(true, null)
                        }
                        .addOnFailureListener { exception ->
                            Log.e(
                                "Cadastro",
                                "Erro ao salvar dados no Firestore: ${exception.message}"
                            )
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

    fun getUserRepository(
        email: String,
        password: String,
        callback: (Boolean, String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Login", "Login realizado com sucesso.")
                callback(true, null)
            } else {
                val errorMessage = task.exception?.localizedMessage ?: "Erro desconhecido."
                Log.e("UserRepository", "Erro ao fazer login: $errorMessage")
                callback(false, "Erro ao fazer login: $errorMessage")
            }
        }
    }

    suspend fun getUser(uid: String): User? {
        Log.d("UserRepository", "Recuperando dados do usuário com UID: $uid")
        return try {
            val document = userCollection.document(uid).get().await()
            if (document.exists()) {
                document.toObject<User>().also {
                    Log.d("UserRepository", "Dados do usuário: $it")
                }
            } else {
                Log.e("UserRepository", "Documento do usuário não encontrado!")
                null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Erro ao recuperar dados do usuário: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    suspend fun reauthenticateUser(password: String): Boolean {
        val currentUser = auth.currentUser ?: return false
        val email = currentUser.email ?: return false

        // Log de credenciais usadas para reautenticação (não faça isso em produção por motivos de segurança)
        Log.d("UserRepository", "Reautenticando usuário com email: $email e senha fornecida.")

        val credential = EmailAuthProvider.getCredential(email, password)
        return try {
            currentUser.reauthenticate(credential).await()
            Log.d("UserRepository", "Reautenticação bem-sucedida.")
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Erro ao reautenticar usuário: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    suspend fun updateUser(user: User, callback: (Boolean, String?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return callback(false, "Usuário não autenticado.")

        try {
            // Atualizar dados no Firestore
            userCollection.document(userId).set(user).await()
            Log.d("UserRepository", "Dados do usuário atualizados no Firestore com sucesso.")
            callback(true, null)
        } catch (e: Exception) {
            Log.e("UserRepository", "Erro ao atualizar dados do usuário: ${e.message}")
            e.printStackTrace()
            callback(false, "Erro ao atualizar dados do usuário: ${e.message}")
        }
    }


}