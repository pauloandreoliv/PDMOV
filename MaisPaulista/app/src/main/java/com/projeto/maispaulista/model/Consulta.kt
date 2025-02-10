package com.projeto.maispaulista.model

data class Consulta(
    val especialidade: String,
    val doutor: String,
    val local: String,
    val data: String,
    val hora: String,
    val disponivel: Boolean
) {
    constructor() : this("", "", "", "", "", false)
}
