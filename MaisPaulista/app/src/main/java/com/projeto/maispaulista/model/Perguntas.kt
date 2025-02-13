package com.projeto.maispaulista.model

data class Perguntas(
    val id: String,
    val pergunta: String,
    val resposta: String
) {
    constructor() : this("", "", "")
}