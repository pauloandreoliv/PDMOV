package com.projeto.maispaulista.service

import com.projeto.maispaulista.model.Contato
import com.projeto.maispaulista.repository.ContatoRepository

class ContatoService {

    private val repository = ContatoRepository()

    fun getContatos(callback: (List<Contato>) -> Unit) {
        repository.getContatos { contatos ->
            callback(contatos)
        }
    }
}
