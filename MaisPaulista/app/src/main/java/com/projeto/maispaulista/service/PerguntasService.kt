package com.projeto.maispaulista.service

import com.projeto.maispaulista.model.Perguntas
import com.projeto.maispaulista.repository.PerguntasRepository

class PerguntasService {

    private val repository = PerguntasRepository()

    fun getPerguntas(callback: (List<Perguntas>) -> Unit) {
        repository.getPerguntas { perguntas ->
            callback(perguntas)
        }
    }
}
