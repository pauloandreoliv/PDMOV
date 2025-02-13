package com.projeto.maispaulista.model

data class Contato(
    val setor: String = "",
    val whatsapp: String = "",
    val telefone: String = "",
    val email: String = ""
){
    constructor() : this("", "", "", "")
}

