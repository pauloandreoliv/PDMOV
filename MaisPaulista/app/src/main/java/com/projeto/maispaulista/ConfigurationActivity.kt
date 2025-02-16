package com.projeto.maispaulista

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.projeto.maispaulista.model.User
import com.projeto.maispaulista.utils.Variaveis
import com.projeto.maispaulista.repository.UserRepository
import com.projeto.maispaulista.service.UserService
import com.projeto.maispaulista.utils.LocationHelper
import com.projeto.maispaulista.utils.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConfigurationActivity : AppCompatActivity() {
    private lateinit var userService: UserService
    private lateinit var nomeEdit: EditText
    private lateinit var cpfEdit: EditText
    private lateinit var emailTextView: TextView
    private var currentPassword: String = ""
    private lateinit var addressEditText: EditText
    private lateinit var locationHelper: LocationHelper
    private var isManualEdit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val userRepository = UserRepository(auth, db)
        userService = UserService(userRepository)

        nomeEdit = findViewById(R.id.nomeEdit)
        cpfEdit = findViewById(R.id.cpfEdit)
        emailTextView = findViewById(R.id.emailTextView)
        addressEditText = findViewById(R.id.addressEditText)
        val mapIcon = findViewById<ImageView>(R.id.mapIcon)
        locationHelper = LocationHelper(this, addressEditText)

        cpfEdit.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            private val mask = "###.###.###-##"

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (isUpdating) {
                    isUpdating = false
                    return
                }

                var str = s.toString().filter { it.isDigit() }

                if (str.length > 11) {
                    str = str.substring(0, 11)
                }

                val formatted = StringBuilder()
                var i = 0
                for (m in mask.toCharArray()) {
                    if (m != '#' && str.length > i) {
                        formatted.append(m)
                        continue
                    }
                    try {
                        formatted.append(str[i])
                    } catch (e: Exception) {
                        break
                    }
                    i++
                }

                isUpdating = true
                cpfEdit.setText(formatted)
                cpfEdit.setSelection(formatted.length)
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.configurationLayout)) { v, insets ->
            val systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backArrow = findViewById<ImageView>(R.id.backArrow)
        backArrow.setOnClickListener {
            val intent = Intent(this, Variaveis.currentActivity)
            startActivity(intent)
            finish()
        }

        window.statusBarColor = androidx.core.content.ContextCompat.getColor(this, android.R.color.white)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        loadUserData()

        mapIcon.setOnClickListener {
            Log.d("ConfigActivity", "Ícone de mapa clicado")
            locationHelper.isManualEdit = false
            locationHelper.checkLocationAndEnableGPS()
        }

        locationHelper.setOnLocationReceivedListener { address ->
            Log.d("ConfigActivity", "Endereço recebido no listener: $address")
            runOnUiThread {
                addressEditText.setText(address)
                addressEditText.error = null // Remove erro caso tenha sido definido antes
            }
        }

        locationHelper.checkLocationAndEnableGPS()


        addressEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                isManualEdit = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Não é necessário fazer nada aqui
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Não é necessário fazer nada aqui
            }
        })

        val registerButton: Button = findViewById(R.id.registerButton)
        registerButton.setOnClickListener {
            if (!NetworkUtils.isNetworkAvailable(this)) {
                NetworkUtils.showNoNetworkDialog(this)
                return@setOnClickListener
            }
            val cpf = cpfEdit.text.toString()
            if (cpf.length < 14) {
                Toast.makeText(baseContext, "CPF incompleto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            updateUserData()
        }

        setupBottomNavigation()
    }

    override fun onBackPressed() {
        val intent = Intent(this, Variaveis.currentActivity)
        startActivity(intent)
        finish()
    }

    private fun loadUserData() {
        val currentUser = Variaveis.uid
        if (currentUser == null) {
            Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("ConfigurationActivity", "Carregando dados para UID: $currentUser")
        CoroutineScope(Dispatchers.Main).launch {
            val user = userService.getUser(currentUser)
            user?.let {
                Log.d("UserData", "Nome: ${it.nome}, Email: ${it.email}")
                nomeEdit.setText(it.nome)
                cpfEdit.setText(it.cpf)
                emailTextView.text = it.email
                addressEditText.setText(it.endereco) // Carrega o endereço do Firestore
                currentPassword = it.password // Armazenar a senha atual
            } ?: Log.e("UserData", "Usuário não encontrado no Firestore!")
        }
    }

    private fun updateUserData() {
        val user = User(
            nome = nomeEdit.text.toString(),
            email = emailTextView.text.toString(),
            password = currentPassword,
            cpf = cpfEdit.text.toString(),
            endereco = addressEditText.text.toString() // Atualiza o endereço com o valor do EditText
        )

        CoroutineScope(Dispatchers.Main).launch {
            // Atualizar os dados do usuário
            userService.updateUser(user) { success, errorMessage ->
                if (success) {
                    showUpdateSuccessDialog()
                } else {
                    Toast.makeText(this@ConfigurationActivity, errorMessage ?: "Erro desconhecido ao atualizar dados", Toast.LENGTH_SHORT).show()
                }
            }
        }
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

    private fun showUpdateSuccessDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sucesso")
        builder.setMessage("Dados atualizados com sucesso.")
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
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

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_settings -> {
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
