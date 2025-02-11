package com.projeto.maispaulista.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.projeto.maispaulista.model.AgendamentoModel
import com.projeto.maispaulista.model.Consulta
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class ConsultaUtils(private val db: FirebaseFirestore) {

    suspend fun updateConsultaStatusIfNeeded() {
        val consultasRef = db.collection("consultas")
        val result = consultasRef.get().await()
        val consultas = result.toObjects(Consulta::class.java)

        consultas.forEach { consulta ->
            if (isDateBeforeToday(consulta.data)) {
                consultasRef.document(consulta.id).update("disponivel", false)
            }
        }
    }

    suspend fun updateAgendamentoStatusIfNeeded() {
        val agendamentosRef = db.collection("consultas_agendadas")
        val result = agendamentosRef.get().await()
        val agendamentos = result.toObjects(AgendamentoModel::class.java)

        agendamentos.forEach { agendamento ->
            if (isDateBeforeToday(agendamento.data)) {
                agendamentosRef.document(agendamento.id).update("Concluida", true).await()
            }
        }
    }

    private fun isDateBeforeToday(dateString: String): Boolean {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = format.parse(dateString)
        return date.before(Date())
    }
}
