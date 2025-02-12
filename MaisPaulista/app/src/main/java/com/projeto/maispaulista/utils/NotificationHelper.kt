package com.projeto.maispaulista.utils

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.projeto.maispaulista.R
import com.projeto.maispaulista.model.AgendamentoModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class NotificationHelper(private val context: Context) {

    fun checkAndSendReminders(agendamentos: List<AgendamentoModel>) {

        agendamentos.forEach { agendamento ->
            Log.d("NotificationHelper", "Agendamento: ${agendamento.data}, Diferença de dias: ${getDaysDifference(agendamento.data)}")
            when (getDaysDifference(agendamento.data)) {
                5L -> sendNotification(
                    "Lembrete de Consulta",
                    "Você tem uma consulta daqui a 5 dias.\n${agendamento.especialidade} no dia ${agendamento.data} às ${agendamento.hora}"
                )
                2L -> sendNotification(
                    "Lembrete de Consulta",
                    "Você tem uma consulta daqui a 2 dias.\n${agendamento.especialidade} no dia ${agendamento.data} às ${agendamento.hora}"
                )
                1L -> sendNotification(
                    "Lembrete de Consulta",
                    "Você tem uma consulta amanhã.\n${agendamento.especialidade} às ${agendamento.hora}"
                )
            }
        }
    }

    fun sendNotification(title: String, content: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Verificar se a permissão de notificação foi concedida
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                showNotification(title, content)
            } else {
                // Solicitar permissão ao usuário
                ActivityCompat.requestPermissions(context as Activity, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_REQUEST_CODE)
            }
        } else {
            // Para versões anteriores ao Android 13, as notificações não precisam de permissão explícita
            showNotification(title, content)
        }
    }


    private fun showNotification(title: String, content: String) {
        val builder = NotificationCompat.Builder(context, "Channel ID")
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(content)
            )
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)

        // Usar um identificador único para cada notificação
        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, builder.build())
    }



    private fun getDaysDifference(dateString: String): Long {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val consultaDate = Calendar.getInstance().apply {
            time = format.parse(dateString) ?: return -1
            set(Calendar.HOUR_OF_DAY, 0)  // Ignora a hora
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val currentDate = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)  // Ignora a hora
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val diffInMillis = consultaDate.timeInMillis - currentDate.timeInMillis
        return TimeUnit.MILLISECONDS.toDays(diffInMillis) // Retorna a diferença em dias
    }


    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Channel Name"
                val descriptionText = "Channel Description"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel("Channel ID", name, importance).apply {
                    description = descriptionText
                }
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}
