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
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.projeto.maispaulista.repository.UserRepository
import com.projeto.maispaulista.service.UserService

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userService: UserService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)

        //instancias
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

        // Configurar clique no TextView para redirecionar à CadastroActivity
        val registerLink = findViewById<TextView>(R.id.registerLink)
        registerLink.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }

        val etEmail = findViewById<EditText>(R.id.emailEditText)
        val etSenha = findViewById<EditText>(R.id.passwordEditText)
        val bntEntrar = findViewById<Button>(R.id.loginButton)



        //clique botão para entrar
        bntEntrar.setOnClickListener {
            val email = etEmail.text.toString()
            val senha = etSenha.text.toString()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(baseContext, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // serviço de login
            userService.verificarUsuario(email, senha){ success, _ ->
                runOnUiThread{
                    if (success) {
                        Toast.makeText(baseContext, "Login Realizado", Toast.LENGTH_SHORT).show()
                        //redirecionar para pagina principal
                        val intent = Intent(this, PrincipalActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(baseContext, "Erro desconhecido durante o Login.", Toast.LENGTH_SHORT).show()
                    }
                }

            }



        }


    }
}