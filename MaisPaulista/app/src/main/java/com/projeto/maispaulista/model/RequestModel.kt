package com.projeto.maispaulista.model

data class RequestModel(
    var id: String = "",
    val userId: String,
    val tipoItem: String,
    val descricao: String,
    val imagemNome: String,
    val status: String,
    val data: String = ""
) {
    constructor() : this("", "", "", "", "", "") // Construtor sem parâmetros necessário para a desserialização
}
