package com.projeto.maispaulista.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class BlogRepository(private val db: FirebaseFirestore) {
    fun getBlogQuery(): Query {
        return db.collection("blogs")
    }
}
