package com.projeto.maispaulista.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.projeto.maispaulista.model.Blog
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BlogRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun getBlogs(): Flow<List<Blog>> = flow {
        val snapshot = firestore.collection("blogs").get().await()
        val blogs = snapshot.toObjects(Blog::class.java)
        emit(blogs)
    }
}