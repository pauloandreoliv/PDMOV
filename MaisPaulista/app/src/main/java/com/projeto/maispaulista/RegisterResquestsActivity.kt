package com.projeto.maispaulista

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.projeto.maispaulista.adapter.Variaveis
import com.projeto.maispaulista.repository.RequestRepository
import com.projeto.maispaulista.service.RequestService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import java.io.File

// Activity para registro de solicitações
class RegisterResquestsActivity : AppCompatActivity() {

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

        val backArrow = findViewById<ImageView>(R.id.backArrow)
        backArrow.setOnClickListener {
            val intent = Intent(this, PrincipalActivity::class.java)
            startActivity(intent)
        }

        window.statusBarColor = ContextCompat.getColor(this, android.R.color.white)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        icCamera = findViewById(R.id.ic_camera)
        icCamera.setOnClickListener {
            openImagePicker()
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.tipo_solicitacao_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            tipoSolicitacaoSpinner.adapter = adapter
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registerResquestsLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imagemButton.setOnClickListener {
            val tipoItem = tipoSolicitacaoSpinner.selectedItem.toString()
            val descricao = descricaoEditText.text.toString()
            val imagemNome = imagemTextView.text.toString()
            salvarSolicitacao(tipoItem, descricao, imagemNome)
        }

        setupBottomNavigation()
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            selectedImageUri?.let {
                val filePath = getRealPathFromURI(it)
                val fileName = filePath?.let { path -> File(path).name }
                imagemTextView.text = fileName ?: "Imagem selecionada"
            }
        }
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        var filePath: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.let {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                filePath = it.getString(columnIndex)
            }
            it.close()
        }
        return filePath
    }


    override fun onBackPressed() {

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun salvarSolicitacao(tipoItem: String, descricao: String, imagemNome: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val success = requestService.addRequest(tipoItem, descricao, imagemNome)
            if (success) {
                showAlertDialog(
                    this@RegisterResquestsActivity,
                    "Solicitação registrada com sucesso",
                    "Acompanhe o status no seviço “Acompanhar solicitação” na tela principal."
                )
            } else {
                Toast.makeText(this@RegisterResquestsActivity, "Erro ao enviar solicitação", Toast.LENGTH_SHORT).show()
            }
        }
    }

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
                    Variaveis.uid = ""
                    true
                }

                else -> false
            }
        }
    }
}