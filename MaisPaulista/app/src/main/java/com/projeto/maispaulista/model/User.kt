package com.projeto.maispaulista.model

data class User(
    val nome: String,
    val email: String,
    val password: String,
    val cpf: String,
    val endereco: String
) {
    // Construtor sem argumentos necessário para Firestore
    constructor() : this("", "", "", "", "")
}
