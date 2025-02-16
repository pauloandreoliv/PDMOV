package com.projeto.maispaulista.model

data class RequestModel(
    var id: String = "",
    var nunSolicitacao: Int = 0,
    val userId: String = "",
    val tipoItem: String = "",
    val descricao: String = "",
    val imagemNome: String = "",
    val status: String = "",
    val data: String = "",
    val endereco: String = ""
) {
    // Construtor sem parâmetros necessário para a desserialização
    constructor() : this("",0, "", "", "", "", "", "", "")
}
