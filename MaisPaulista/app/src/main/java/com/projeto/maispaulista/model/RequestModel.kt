package com.projeto.maispaulista.model

data class RequestModel(
    val userId: String,
    val tipoItem: String,
    val descricao: String,
    val imagemNome: String // Nome do arquivo de imagem
) {
    constructor() : this("", "", "", "") // Construtor sem parâmetros necessário para a desserialização
}
