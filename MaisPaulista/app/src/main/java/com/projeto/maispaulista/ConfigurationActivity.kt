package com.projeto.maispaulista

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConfigurationActivity : AppCompatActivity() {
    private lateinit var userService: UserService
    private lateinit var nomeEdit: EditText
    private lateinit var cpfEdit: EditText
    private lateinit var emailTextView: TextView
    private var currentPassword: String = ""

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.configurationLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backArrow = findViewById<ImageView>(R.id.backArrow)
        backArrow.setOnClickListener {
            val intent = Intent(this, Variaveis.currentActivity)
            startActivity(intent)
            finish()
        }

        window.statusBarColor = ContextCompat.getColor(this, android.R.color.white)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }


        loadUserData()
        val registerButton: Button = findViewById(R.id.registerButton)
        registerButton.setOnClickListener {
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
                cpfEdit.setText(it.cpfCnpj)
                emailTextView.text = it.email
                currentPassword = it.password // Armazenar a senha atual
            } ?: Log.e("UserData", "Usuário não encontrado no Firestore!")
        }
    }


    private fun updateUserData() {
        val user = User(
            nome = nomeEdit.text.toString(),
            email = emailTextView.text.toString(),
            password = currentPassword, // Usar a senha atual
            cpfCnpj = cpfEdit.text.toString()
        )

        CoroutineScope(Dispatchers.Main).launch {
            // Atualizar os dados do usuário
            userService.updateUser(user) { success, errorMessage ->
                if (success) {
                    Toast.makeText(this@ConfigurationActivity, "Dados atualizados com sucesso", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ConfigurationActivity, errorMessage ?: "Erro desconhecido ao atualizar dados", Toast.LENGTH_SHORT).show()
                }
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