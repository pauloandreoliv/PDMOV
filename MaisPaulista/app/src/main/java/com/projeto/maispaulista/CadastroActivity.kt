package com.projeto.maispaulista


import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.projeto.maispaulista.repository.UserRepository
import com.projeto.maispaulista.service.UserService
import com.projeto.maispaulista.utils.LocationHelper
import com.projeto.maispaulista.utils.Variaveis

class CadastroActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userService: UserService
    private lateinit var addressEditText: EditText
    private lateinit var locationHelper: LocationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val userRepository = UserRepository(auth, db)
        userService = UserService(userRepository)

        setContentView(R.layout.activity_registration)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cadastroLayout)) { v, insets ->
            val systemBars =
                insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.statusBarColor = androidx.core.content.ContextCompat.getColor(this, android.R.color.white)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        val backArrow = findViewById<ImageView>(R.id.backArrow)
        backArrow.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val etEmail = findViewById<EditText>(R.id.emailEditText)
        val etSenha = findViewById<EditText>(R.id.passwordEditText)
        val etNome = findViewById<EditText>(R.id.nameEditText)
        val etCpfCNPJ = findViewById<EditText>(R.id.cpfCnpjEditText)
        val btnCadastrar = findViewById<Button>(R.id.registerButton)
        addressEditText = findViewById(R.id.addressEditText)
        val mapIcon = findViewById<ImageView>(R.id.mapIcon)
        locationHelper = LocationHelper(this, addressEditText)

        btnCadastrar.setOnClickListener {
            val email = etEmail.text.toString()
            val senha = etSenha.text.toString()
            val nome = etNome.text.toString()
            val cpfCnpj = etCpfCNPJ.text.toString()

            if (email.isEmpty() || senha.isEmpty() || nome.isEmpty() || cpfCnpj.isEmpty()) {
                Toast.makeText(baseContext, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha.length < 6) {
                Toast.makeText(
                    baseContext,
                    "A senha deve ter no mínimo 6 caracteres!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            userService.cadastrarUsuario(email, senha, nome, cpfCnpj) { success, _ ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(
                            baseContext,
                            "Cadastro realizado com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            baseContext,
                            "Erro desconhecido durante o cadastro.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        mapIcon.setOnClickListener {
            locationHelper.isManualEdit = false
            locationHelper.checkLocationAndEnableGPS()
        }

        // Verifica permissão e GPS ao abrir a tela
        locationHelper.checkLocationAndEnableGPS()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LocationHelper.LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.d("LocationPermission", "Permissão concedida")
                // Verifica o GPS após a permissão ser concedida
                if (locationHelper.isLocationEnabled()) {
                    locationHelper.startLocationUpdates()
                } else {
                    locationHelper.enableGPS()
                }
            } else {
                Log.d("LocationPermission", "Permissão negada")
                Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LocationHelper.LOCATION_SETTINGS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // GPS ativado, pode prosseguir
                locationHelper.startLocationUpdates()
            } else {
                Toast.makeText(this, "GPS não ativado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, Variaveis.currentActivity)
        startActivity(intent)
        finish()
    }
}
