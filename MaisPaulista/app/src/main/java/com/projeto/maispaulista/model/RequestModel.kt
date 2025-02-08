package com.projeto.maispaulista.model

data class RequestModel(
    val userId: String,
    val tipoItem: String,
    val descricao: String,
    val imagemNome: String,//
    val status: String
) {
    constructor() : this("", "", "", "", "") // Construtor sem parâmetros necessário para a desserialização
}
