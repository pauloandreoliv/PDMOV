package com.projeto.maispaulista.utils

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking

class NotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        // Inicializar Firestore e NotificationHelper
        Log.d("NotificationWorker", "Worker Executado")
        val db = FirebaseFirestore.getInstance()
        val notificationHelper = NotificationHelper(applicationContext)
        val consultaUtils = ConsultaUtils(db, notificationHelper)

        // Verificar e enviar lembretes utilizando o UID de Variaveis
        runBlocking {
            consultaUtils.updateAgendamentoStatusIfNeeded()
        }

        // Indicar que o trabalho foi concluído com sucesso
        return Result.success()
    }
}