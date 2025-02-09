package com.projeto.maispaulista

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.projeto.maispaulista.repository.UserRepository
import com.projeto.maispaulista.service.UserService
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.projeto.maispaulista.adapter.Variaveis


class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userService: UserService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)

        // Instancias
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val userRepository = UserRepository(auth, db)
        userService = UserService(userRepository)

        // Configurar insets para o layout raiz
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar a cor de status bar
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.white)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        // Configurar clique no TextView para redirecionar à CadastroActivity
        val registerLink = findViewById<TextView>(R.id.registerLink)
        registerLink.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }

        fun showAlertDialog(context: Context, title: String, message: String) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(title)
            builder.setMessage(message)
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        val etEmail = findViewById<EditText>(R.id.emailEditText)
        val etSenha = findViewById<EditText>(R.id.passwordEditText)
        val bntEntrar = findViewById<Button>(R.id.loginButton)

        // Clique no botão para entrar
        bntEntrar.setOnClickListener {
            val email = etEmail.text.toString()
            val senha = etSenha.text.toString()

            if (email.isEmpty() || senha.isEmpty()) {
                showAlertDialog(this, "Informação login incompleto", "Preencha todos os campos!")
                return@setOnClickListener
            }

            // Serviço de login
            userService.verificarUsuario(email, senha) { success, errorMessage ->
                runOnUiThread {
                    if (success) {
                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            val uid = currentUser.uid
                            Variaveis.uid = uid  // Armazene o UID no Singleton
                            Log.d("Login", "Login realizado com sucesso. UID: $uid")

                            // Exibir alerta de sucesso e redirecionar
                            showAlertDialog(this, "Login Realizado", "Login realizado com sucesso!")
                            Handler(Looper.getMainLooper()).postDelayed({
                                val intent = Intent(this, PrincipalActivity::class.java)
                                startActivity(intent)
                                finish()
                            }, 3000)
                        } else {
                            showAlertDialog(this, "Erro de Login", "Erro ao obter informações do usuário.")
                        }
                    } else {
                        showAlertDialog(this, "Erro de Login", "Erro: $errorMessage")
                    }
                }
            }
        }
    }
}