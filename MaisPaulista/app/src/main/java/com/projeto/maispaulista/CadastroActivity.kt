package com.projeto.maispaulista

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.projeto.maispaulista.model.User
import com.projeto.maispaulista.repository.UserRepository
import com.projeto.maispaulista.service.UserService

class CadastroActivity : AppCompatActivity() {

    //inicialização
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userService: UserService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        //instancias
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val userRepository = UserRepository(auth, db)
        userService = UserService(userRepository)


        setContentView(R.layout.activity_cadastro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cadastroLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

        //retorno pagina principal
        val backArrow = findViewById<ImageView>(R.id.backArrow)
        backArrow.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        //dados da interface
        val etEmail = findViewById<EditText>(R.id.emailEditText)
        val etSenha = findViewById<EditText>(R.id.passwordEditText)
        val etNome = findViewById<EditText>(R.id.nameEditText)
        val etCpfCNPJ = findViewById<EditText>(R.id.cpfCnpjEditText)
        val btnCadastrar = findViewById<Button>(R.id.registerButton)


        //clique
        btnCadastrar.setOnClickListener {
            val email = etEmail.text.toString()
            val senha = etSenha.text.toString()
            val nome = etNome.text.toString()
            val cpfCnpj = etCpfCNPJ.text.toString()

            // serviço cadastrar o usuário
            userService.cadastrarUsuario(email, senha, nome, cpfCnpj) { success, error ->
                if (success) {
                    Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT)
                        .show()
                    // retorno pagina principal
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }

            }
        }

    }
}


