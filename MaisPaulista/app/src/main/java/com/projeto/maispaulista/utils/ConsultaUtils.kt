package com.projeto.maispaulista.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.projeto.maispaulista.model.Agendamento
import com.projeto.maispaulista.model.Consulta
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

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
        val agendamentos = result.toObjects(Agendamento::class.java)

        val agendamentosFuturos = mutableListOf<Agendamento>()

        result.documents.forEach { document ->
            val agendamento = document.toObject(Agendamento::class.java)
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
