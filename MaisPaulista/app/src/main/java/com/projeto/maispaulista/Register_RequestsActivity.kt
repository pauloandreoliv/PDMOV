package com.projeto.maispaulista

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.projeto.maispaulista.utils.Variaveis
import com.projeto.maispaulista.repository.RequestRepository
import com.projeto.maispaulista.service.RequestService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import android.Manifest


class Register_RequestsActivity   : AppCompatActivity() {
    private val REQUEST_WRITE_STORAGE = 112

    private val requestService: RequestService by lazy {
        val firestore = FirebaseFirestore.getInstance()
        val userId = Variaveis.uid ?: ""
        RequestService(RequestRepository(firestore), userId)
    }

    private lateinit var tipoSolicitacaoSpinner: Spinner
    private lateinit var descricaoEditText: EditText
    private lateinit var imagemTextView: TextView
    private lateinit var imagemButton: Button

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var icCamera: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_requests)

        tipoSolicitacaoSpinner = findViewById(R.id.tipoSolicitacaoSpinner)
        descricaoEditText = findViewById(R.id.DescriçãoEdit)
        imagemTextView = findViewById(R.id.imagemTextView)
        imagemButton = findViewById(R.id.registerButton)

        checkStoragePermissions()

        // Configurar o clique no botão de voltar
        val backArrow = findViewById<ImageView>(R.id.backArrow)
        backArrow.setOnClickListener {
            val intent = Intent(this, PrincipalActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Configurar a cor de status bar
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.white)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        // Configurar o clique no botão de câmera
        icCamera = findViewById(R.id.ic_camera)
        icCamera.setOnClickListener {
            openImagePicker()
        }

        // Configurar o Spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.tipo_solicitacao_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            tipoSolicitacaoSpinner.adapter = adapter
        }

        // Configurar o padding da view
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registerResquestsLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar o clique no botão de registro
        imagemButton.setOnClickListener {
            val tipoItem = tipoSolicitacaoSpinner.selectedItem.toString()
            val descricao = descricaoEditText.text.toString()
            val imagemNome = imagemTextView.text.toString()


            if (tipoItem.isNotEmpty() && descricao.isNotEmpty() && imagemNome.isNotEmpty()) {
                salvarSolicitacao(tipoItem, descricao, imagemNome)
            } else {
                showAlertDialog(
                    this@Register_RequestsActivity,
                    "Campos Incompletos",
                    "Por favor, preencha todos os campos."
                )
            }
        }

        setupBottomNavigation()
    }

    // Sobrescrever  onBackPressed para voltar para a tela principal
    override fun onBackPressed() {
        val intent = Intent(this, PrincipalActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Função para salvar a solicitação
    private fun salvarSolicitacao(tipoItem: String, descricao: String, imagemNome: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val success = requestService.addRequest(tipoItem, descricao, imagemNome)
            if (success) {
                showAlertDialog(
                    this@Register_RequestsActivity,
                    "Solicitação registrada com sucesso",
                    "Acompanhe o status no serviço “Acompanhar solicitação” na tela principal."
                )
            } else {
                Toast.makeText(
                    this@Register_RequestsActivity,
                    "Erro ao enviar solicitação",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    // Função para exibir um AlertDialog
    private fun showAlertDialog(context: Context, title: String, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


    // Função para abrir o seletor de imagens
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Solicitar permissão de armazenamento
    private fun checkStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_IMAGES), REQUEST_WRITE_STORAGE)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_WRITE_STORAGE)
            }
        }
    }


    // Sobrescrever  onActivityResult para lidar com a seleção de imagem
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            selectedImageUri?.let {
                // Verificar a permissão antes de salvar a imagem
                val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_IMAGES
                } else {
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }

                if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                    val imageName = saveImageToCustomFolder(it)
                    imagemTextView.text = imageName
                } else {
                    // Solicitar permissão novamente
                    requestPermissions(arrayOf(permission), REQUEST_WRITE_STORAGE)
                }
            }
        }
    }

    // Sobrescrever  onRequestPermissionsResult para lidar com a resposta da solicitação de permissão
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_WRITE_STORAGE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permissão de armazenamento concedida", Toast.LENGTH_SHORT).show()
        } else {
            // Permissão negada
            Toast.makeText(
                this,
                "Permissão de armazenamento negada. O aplicativo pode não funcionar corretamente.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Função para salvar a imagem no diretório personalizado
    private fun saveImageToCustomFolder(imageUri: Uri): String {
        val inputStream = contentResolver.openInputStream(imageUri)
        val originalFileName = getFileNameFromUri(imageUri) ?: "imagem_${System.currentTimeMillis()}.jpg"
        val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

        // Caminho correto no armazenamento interno do app
        val imagesFolder = File(filesDir, "Banco_imagens_solicitacao")
        if (!imagesFolder.exists()) {
            imagesFolder.mkdirs()
        }

        val jpegFileName = originalFileName.replace(".heic", ".jpg", true)
        val imageFile = File(imagesFolder, jpegFileName)
        val outputStream = FileOutputStream(imageFile)

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        return jpegFileName
    }

    // Função para obter o nome do arquivo a partir do URI
    private fun getFileNameFromUri(uri: Uri): String? {
        var fileName: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                fileName = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            }
        }
        return fileName }

    // Função para configurar a navegação inferior
    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_settings -> {
                    Variaveis.currentActivity = this::class.java
                    startActivity(Intent(this, ConfigurationActivity::class.java))
                    true
                }

                R.id.navigation_home -> {
                    startActivity(Intent(this, PrincipalActivity::class.java))
                    true
                }

                R.id.navigation_back -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    Variaveis.uid = null
                    true
                }

                else -> false
            }
        }
    }
}