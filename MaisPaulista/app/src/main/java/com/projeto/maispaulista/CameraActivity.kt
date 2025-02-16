package com.projeto.maispaulista


import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.projeto.maispaulista.utils.ImageUtils
import java.io.File

class CameraActivity : AppCompatActivity() {
    private lateinit var cameraPreview: PreviewView
    private lateinit var captureButton: ImageButton
    private lateinit var cancelButton: ImageButton
    private lateinit var galleryButton: ImageButton
    private lateinit var imageCapture: ImageCapture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_camera)

        cameraPreview = findViewById(R.id.camera_preview)
        captureButton = findViewById(R.id.button_capture)
        cancelButton = findViewById(R.id.button_cancel)
        galleryButton = findViewById(R.id.button_gallery)

        if (!ImageUtils.checkCameraPermissions(this)) {
            ImageUtils.requestCameraPermissions(this)
        } else {
            startCameraPreview()
        }

        captureButton.setOnClickListener {
            takePicture()
        }

        cancelButton.setOnClickListener {
            finish()
        }

        galleryButton.setOnClickListener {
            if (ImageUtils.checkStoragePermissions(this)) {
                openImagePicker()
            } else {
                ImageUtils.requestStoragePermissions(this)
            }
        }
    }

    private fun startCameraPreview() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            preview.setSurfaceProvider(cameraPreview.surfaceProvider)

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePicture() {
        val imageName = "imagem_${System.currentTimeMillis()}.jpg"
        val imageFile = File(getExternalFilesDir(null), imageName)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(imageFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                    Toast.makeText(this@CameraActivity, "Erro ao tirar foto", Toast.LENGTH_LONG).show()
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(imageFile)
                    val imageName = ImageUtils.saveImageToCustomFolder(this@CameraActivity, savedUri)
                    val intent = Intent()
                    intent.putExtra("imageName", imageName)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        )
    }
    override fun onBackPressed() {
        val intent = Intent(this, PrincipalActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, ImageUtils.PICK_IMAGE_REQUEST)
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            ImageUtils.REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCameraPreview()
                } else {
                    showPermissionDeniedDialog("Permissão da câmera é necessária para tirar fotos.")
                }
            }
            ImageUtils.REQUEST_WRITE_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImagePicker()
                } else {
                    showPermissionDeniedDialog("Permissão de armazenamento é necessária para selecionar imagens da galeria.")
                }
            }
        }
    }

    private fun showPermissionDeniedDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permissão Negada")
        builder.setMessage("$message Por favor, conceda a permissão nas configurações do aplicativo.")
        builder.setPositiveButton("Configurações") { dialog, _ ->
            dialog.dismiss()
            openAppSettings()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ImageUtils.PICK_IMAGE_REQUEST -> {
                    data?.data?.let { uri ->
                        val imageName = ImageUtils.saveImageToCustomFolder(this, uri)
                        val intent = Intent()
                        intent.putExtra("imageName", imageName)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }
                ImageUtils.REQUEST_IMAGE_CAPTURE -> {
                    val imageName = data?.getStringExtra("imageName")
                    val intent = Intent()
                    intent.putExtra("imageName", imageName)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        }
    }
}
