package com.projeto.maispaulista

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.projeto.maispaulista.repository.RequestRepository
import com.projeto.maispaulista.service.RequestService
import com.projeto.maispaulista.utils.AuthUtils
import com.projeto.maispaulista.utils.ImageUtils
import com.projeto.maispaulista.utils.LocationHelper
import com.projeto.maispaulista.utils.NetworkUtils
import com.projeto.maispaulista.utils.Variaveis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Register_RequestsActivity : AppCompatActivity() {
    private val requestService: RequestService by lazy {
        val firestore = FirebaseFirestore.getInstance()
        val userId = Variaveis.uid ?: ""
        RequestService(RequestRepository(firestore), userId)
    }

    private lateinit var tipoSolicitacaoSpinner: Spinner
    private lateinit var descricaoEditText: EditText
    private lateinit var imagemTextView: TextView
    private lateinit var imagemButton: Button
    private lateinit var icCamera: ImageView
    private lateinit var addressEditText: EditText
    private lateinit var locationHelper: LocationHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_requests)
        AuthUtils.checkAuthentication(this)

        tipoSolicitacaoSpinner = findViewById(R.id.tipoSolicitacaoSpinner)
        descricaoEditText = findViewById(R.id.DescriçãoEdit)
        imagemTextView = findViewById(R.id.imagemTextView)
        imagemButton = findViewById(R.id.registerButton)
        icCamera = findViewById(R.id.cameraImageView)
        addressEditText = findViewById(R.id.addressEditText)
        val mapIcon = findViewById<ImageView>(R.id.mapIcon)
        locationHelper = LocationHelper(this, addressEditText)


        icCamera.setOnClickListener {
            if (ImageUtils.checkCameraPermissions(this)) {
                openCameraActivity()
            } else {
                ImageUtils.requestCameraPermissions(this)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registerResquestsLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Configurar a cor de status bar
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.white)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        val backArrow = findViewById<ImageView>(R.id.backArrow)
        backArrow.setOnClickListener {
            val intent = Intent(this, PrincipalActivity::class.java)
            startActivity(intent)
            finish()
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

        imagemButton.setOnClickListener {

            if (!NetworkUtils.isNetworkAvailable(this)) {
                NetworkUtils.showNoNetworkDialog(this)
                return@setOnClickListener
            }

            val tipoItem = tipoSolicitacaoSpinner.selectedItem.toString()
            val descricao = descricaoEditText.text.toString()
            val endereco = addressEditText.text.toString()
            val imagemNome = imagemTextView.text.toString()

            if (tipoItem.isNotEmpty() && descricao.isNotEmpty() && imagemNome.isNotEmpty() && endereco.isNotEmpty()) {
                salvarSolicitacao(tipoItem, descricao, imagemNome, endereco)
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_LONG).show()
            }
        }


        mapIcon.setOnClickListener {
            locationHelper.isManualEdit = false
            locationHelper.checkLocationAndEnableGPS()
        }

        // Verifica permissão e GPS ao abrir a tela
        locationHelper.checkLocationAndEnableGPS()
        setupBottomNavigation()
    }


    override fun onBackPressed() {
        val intent = Intent(this, PrincipalActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun salvarSolicitacao(tipoItem: String, descricao: String, imagemNome: String, endereco: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val success = requestService.addRequest(tipoItem, descricao, imagemNome, endereco)
            if (success) {
                showAlertDialog(
                    this@Register_RequestsActivity,
                    "Sucesso",
                    "Solicitação registrada com sucesso!"
                )
            } else {
                Toast.makeText(this@Register_RequestsActivity, "Erro ao enviar solicitação.", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun openCameraActivity() {
        try {
            val intent = Intent(this, CameraActivity::class.java)
            startActivityForResult(intent, ImageUtils.REQUEST_IMAGE_CAPTURE)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Erro ao abrir a câmera", Toast.LENGTH_LONG).show()
        }
    }


    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, ImageUtils.PICK_IMAGE_REQUEST)
    }



    override fun onResume() {
        super.onResume()
        // Verifica permissão de localização e atualiza a localização atual se a permissão foi concedida
        if (locationHelper.checkLocationPermissions()) {
            if (locationHelper.isLocationEnabled()) {
                locationHelper.startLocationUpdates()
            } else {
                locationHelper.enableGPS()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            ImageUtils.REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCameraActivity()
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
            LocationHelper.LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("LocationPermission", "Permissão concedida")
                    // Verifica o GPS após a permissão ser concedida
                    if (locationHelper.isLocationEnabled()) {
                        locationHelper.startLocationUpdates()
                    } else {
                        locationHelper.enableGPS()
                    }
                } else {
                    Log.d("LocationPermission", "Permissão negada")
                    showPermissionDeniedDialog("Permissão de localização é necessária para obter sua localização atual.")
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            LocationHelper.LOCATION_SETTINGS_REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    // GPS ativado, pode prosseguir
                    locationHelper.startLocationUpdates()
                } else {
                    Toast.makeText(this, "GPS não ativado", Toast.LENGTH_SHORT).show()
                }
            }

            ImageUtils.PICK_IMAGE_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { uri ->
                        val imageName = ImageUtils.saveImageToCustomFolder(this, uri)
                        imagemTextView.text = imageName
                    }
                }
            }

            ImageUtils.REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val imageName = data?.getStringExtra("imageName")
                    imagemTextView.text = imageName
                }
            }
        }
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
                    FirebaseAuth.getInstance().signOut() // Sai da autenticação
                    Variaveis.uid = null
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }
}
