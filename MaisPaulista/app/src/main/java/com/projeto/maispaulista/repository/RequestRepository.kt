package com.projeto.maispaulista.repository

import com.google.firebase.firestore.FirebaseFirestore

import com.projeto.maispaulista.model.RequestModel
import kotlinx.coroutines.tasks.await


class RequestRepository(private val firestore: FirebaseFirestore) {


    // Função para adicionar uma solicitação ao Firestore
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

    // Função para obter todas as solicitações do Firestore
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

    // Função para contar documentos em uma coleção do Firestore
        suspend fun countDocumentsInCollection(collectionName: String): Long {
            return try {
                val snapshot = firestore.collection(collectionName).get().await()
                snapshot.size().toLong()
            } catch (e: Exception) {
                0L
            }
        }

    // Função para obter uma solicitação pelo ID do Firestore
    suspend fun getRequestById(id: String): RequestModel? {
        return try {
            val document = firestore.collection("requests").document(id).get().await()
            document.toObject(RequestModel::class.java)
        } catch (e: Exception) {
            null
        }
    }


}
