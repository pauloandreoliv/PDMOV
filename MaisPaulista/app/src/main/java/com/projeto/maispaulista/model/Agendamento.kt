package com.projeto.maispaulista.model

class Agendamento (
    val id: String = "",
    val uid: String = "",
    val especialidade: String = "",
    val doutor: String = "",
    val local: String = "",
    val data: String = "",
    val hora: String = "",
    val Concluida: Boolean

){
    constructor() : this("", "", "", "", "", "", "", false)
}