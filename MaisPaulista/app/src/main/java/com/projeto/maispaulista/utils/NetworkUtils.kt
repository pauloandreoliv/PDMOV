package com.projeto.maispaulista.utils

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AlertDialog

object NetworkUtils {

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    fun showNoNetworkDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Sem Conexão")
        builder.setMessage("Não há conexão com a internet. Verifique sua conexão e tente novamente.")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
