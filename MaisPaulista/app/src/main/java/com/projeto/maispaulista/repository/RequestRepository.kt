package com.projeto.maispaulista.repository

import com.google.firebase.firestore.FirebaseFirestore

import com.projeto.maispaulista.model.RequestModel
import kotlinx.coroutines.tasks.await


class RequestRepository(private val firestore: FirebaseFirestore) {


        suspend fun addRequest(request: RequestModel): Boolean {
            return try {
                val documentReference = firestore.collection("requests").document()
                request.id = documentReference.id
                documentReference.set(request).await()
                true
            } catch (e: Exception) {
                false
            }
        }

        suspend fun getRequestsByUser(userId: String): List<RequestModel> {
            return try {
                val snapshot = firestore.collection("requests")
                    .whereEqualTo("userId", userId)
                    .get().await()

                snapshot.toObjects(RequestModel::class.java)
            } catch (e: Exception) {
                emptyList()
            }
        }


        suspend fun countDocumentsInCollection(collectionName: String): Long {
            return try {
                val snapshot = firestore.collection(collectionName).get().await()
                snapshot.size().toLong()
            } catch (e: Exception) {
                0L
            }
        }

    suspend fun getRequestById(id: String): RequestModel? {
        return try {
            val document = firestore.collection("requests").document(id).get().await()
            document.toObject(RequestModel::class.java)
        } catch (e: Exception) {
            null
        }
    }


}
