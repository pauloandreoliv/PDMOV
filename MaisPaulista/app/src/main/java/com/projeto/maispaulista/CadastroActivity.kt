package com.projeto.maispaulista

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.projeto.maispaulista.utils.Variaveis
import com.projeto.maispaulista.repository.UserRepository
import com.projeto.maispaulista.service.UserService



class CadastroActivity : AppCompatActivity() {

    //inicialização
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userService: UserService
    private lateinit var addressLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)

        //instancias
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val userRepository = UserRepository(auth, db)
        userService = UserService(userRepository)


        setContentView(R.layout.activity_registration)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cadastroLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

        // Configurar a cor de status bar
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.white)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
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
        addressLayout = findViewById(R.id.addressLayout)


        //clique de cadastro
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
                Toast.makeText(baseContext, "A senha deve ter no mínimo 6 caracteres!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // serviço cadastrar o usuário
            userService.cadastrarUsuario(email, senha, nome, cpfCnpj) { success, _ ->

                runOnUiThread{
                    if (success) {
                        Toast.makeText(baseContext, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                        // retorno pagina principal
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(baseContext, "Erro desconhecido durante o cadastro.", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }

    }

    override fun onBackPressed() {

        val intent = Intent(this, Variaveis.currentActivity)
        startActivity(intent)
        finish()
    }

}


