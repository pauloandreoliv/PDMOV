package com.projeto.maispaulista.utils

import android.app.Activity
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.projeto.maispaulista.MainActivity

object AuthUtils {
    // Função para verificar se o usuário está autenticado
    fun checkAuthentication(activity: Activity) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            // Usuário não está autenticado, redirecionar para MainActivity
            val intent = Intent(activity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(intent)
            activity.finish()
        }
    }
}
