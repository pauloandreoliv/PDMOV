package com.projeto.maispaulista.utils

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.projeto.maispaulista.R
import com.projeto.maispaulista.model.AgendamentoModel
import com.projeto.maispaulista.model.Consulta
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.jar.Manifest

class ConsultaUtils(
    private val db: FirebaseFirestore,
    private val notificationHelper: NotificationHelper
) {

    suspend fun updateConsultaStatusIfNeeded() {
        val consultasRef = db.collection("consultas")
        val result = consultasRef.get().await()
        val consultas = result.toObjects(Consulta::class.java)

        consultas.forEach { consulta ->
            if (isDateBeforeToday(consulta.data)) {
                consultasRef.document(consulta.id).update("disponivel", false).await()
            }
        }
    }

    suspend fun updateAgendamentoStatusIfNeeded() {
        val uid = Variaveis.uid ?: return
        val agendamentosRef = db.collection("consultas_agendadas").whereEqualTo("uid", uid)
        val result = agendamentosRef.get().await()
        val agendamentos = result.toObjects(AgendamentoModel::class.java)

        val agendamentosFuturos = mutableListOf<AgendamentoModel>()

        result.documents.forEach { document ->
            val agendamento = document.toObject(AgendamentoModel::class.java)
            if (agendamento != null) {
                if (isDateBeforeToday(agendamento.data)) {
                    document.reference.update("Concluida", true).await()
                } else {
                    agendamentosFuturos.add(agendamento) // Adiciona para notificação
                }
            }
        }

        // Agora passa todos os agendamentos futuros corretamente
        if (agendamentosFuturos.isNotEmpty()) {
            notificationHelper.checkAndSendReminders(agendamentosFuturos)
        }
    }

    private fun isDateBeforeToday(dateString: String): Boolean {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = format.parse(dateString)
        return date.before(Date())
    }
}
