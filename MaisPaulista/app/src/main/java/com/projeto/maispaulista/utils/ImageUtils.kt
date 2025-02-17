package com.projeto.maispaulista.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.FileOutputStream

object ImageUtils {

    const val REQUEST_WRITE_STORAGE = 112
    const val REQUEST_CAMERA_PERMISSION = 113
    const val PICK_IMAGE_REQUEST = 1
    const val REQUEST_IMAGE_CAPTURE = 114

    // Função para verificar permissões de armazenamento
    fun checkStoragePermissions(activity: Activity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    // Função para verificar permissões de câmera
    fun checkCameraPermissions(activity: Activity): Boolean {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    // Função para solicitar permissões de câmera
    fun requestCameraPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
    }

    // Função para solicitar permissões de armazenamento
    fun requestStoragePermissions(activity: Activity) {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        ActivityCompat.requestPermissions(activity, arrayOf(permission), REQUEST_WRITE_STORAGE)
    }

    // Função para exibir um diálogo de permissão negada
    fun saveImageToCustomFolder(context: Context, imageUri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val originalFileName = getFileNameFromUri(context, imageUri) ?: "imagem_${System.currentTimeMillis()}.jpg"
        val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

        val imagesFolder = File(context.filesDir, "Banco_imagens_solicitacao")
        if (!imagesFolder.exists()) imagesFolder.mkdirs()

        val jpegFileName = originalFileName.replace(".heic", ".jpg", true)
        val imageFile = File(imagesFolder, jpegFileName)
        val outputStream = FileOutputStream(imageFile)

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        return jpegFileName
    }

    // Função para obter o nome do arquivo a partir do URI
    private fun getFileNameFromUri(context: Context, uri: Uri): String? {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            if (it.moveToFirst()) it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            else null
        }
    }
}
