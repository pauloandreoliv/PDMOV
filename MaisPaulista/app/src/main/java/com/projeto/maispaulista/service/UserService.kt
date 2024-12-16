package com.projeto.maispaulista.service

import android.util.Log
import com.projeto.maispaulista.model.User
import com.projeto.maispaulista.repository.UserRepository


class UserService(private val repository: UserRepository) {


    fun cadastrarUsuario(
        email: String,
        password: String,
        nome: String,
        cpfCnpj: String,
        callback: (Boolean, String?) -> Unit
    ) {
        if (email.isEmpty() || password.isEmpty() || nome.isEmpty() || cpfCnpj.isEmpty()) {
            callback(false, "Por favor, preencha todos os campos.")
            return
        }

        val user = User(nome = nome, email = email, password = password, cpfCnpj = cpfCnpj)
        repository.createUser(email, password, user) { success, error ->
            if (success) {
                Log.d("UserService", "Usuário cadastrado com sucesso.")
                callback(true, null)
            } else {
                Log.e("UserService", "Erro ao cadastrar usuário: $error")
                callback(false, error)
            }
        }
    }
}
