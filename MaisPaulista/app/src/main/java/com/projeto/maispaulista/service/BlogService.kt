package com.projeto.maispaulista.service

import com.google.firebase.firestore.Query
import com.projeto.maispaulista.model.Blog
import com.projeto.maispaulista.repository.BlogRepository

class BlogService(private val repository: BlogRepository) {
    fun getBlogQuery(): Query {
        return repository.getBlogQuery()
    }
}

