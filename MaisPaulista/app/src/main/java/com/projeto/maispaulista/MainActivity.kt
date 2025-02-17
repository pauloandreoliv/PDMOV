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
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.projeto.maispaulista.utils.AuthUtils
import com.projeto.maispaulista.utils.ConsultaUtils
import com.projeto.maispaulista.utils.NetworkUtils
import com.projeto.maispaulista.utils.NotificationHelper
import com.projeto.maispaulista.utils.NotificationWorker
import com.projeto.maispaulista.utils.Variaveis
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userService: UserService

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    }

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

        checkNotificationPermission()
        if (!NetworkUtils.isNetworkAvailable(this)) {
            NetworkUtils.showNoNetworkDialog(this)
        }

        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val eyeImageView: ImageView = findViewById(R.id.eyeImageView)

        // Configurar o clique no ImageView para alternar a visibilidade da senha
        eyeImageView.setOnClickListener {
            if (passwordEditText.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                // Ocultar senha
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                eyeImageView.setImageResource(R.drawable.ic_olho_fechado)
            } else {
                // Mostrar senha
                passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                eyeImageView.setImageResource(R.drawable.ic_olho_aberto)
            }
            // Mover o cursor para o final do texto
            passwordEditText.setSelection(passwordEditText.text.length)
        }

        // Configurar a cor de status bar
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.white)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout  )) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        // Configurar insets para o layout raiz
        configureInsets()

        // Criação do canal de notificação
        NotificationHelper.createNotificationChannel(this)

        // Configurar a cor de status bar
        configureStatusBar()

        // Configurar clique no TextView para redirecionar à CadastroActivity
        configureRegisterLink()

        val etEmail = findViewById<EditText>(R.id.emailEditText)
        val etSenha = findViewById<EditText>(R.id.passwordEditText)
        val bntEntrar = findViewById<Button>(R.id.loginButton)

        bntEntrar.setOnClickListener {
            handleLogin(etEmail, etSenha)
        }


    }

    private fun configureInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    private fun configureStatusBar() {
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.white)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    private fun configureRegisterLink() {
        val registerLink = findViewById<TextView>(R.id.registerLink)
        registerLink.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }
    }

    // Metodo para lidar com o clique no botão de login
    private fun handleLogin(etEmail: EditText, etSenha: EditText) {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            NetworkUtils.showNoNetworkDialog(this)
            return
        }

        val email = etEmail.text.toString()
        val senha = etSenha.text.toString()
        if (email.isEmpty() || senha.isEmpty()) {
            showAlertDialog(this, "Informação login incompleto", "Preencha todos os campos!")
            return
        }

        // Serviço de login
        userService.verificarUsuario(email, senha) { success, errorMessage ->
            runOnUiThread {
                if (success) {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        val uid = currentUser.uid
                        Variaveis.uid = uid // Armazene o UID no Singleton
                        // Configuração do WorkManager
                        configureWorkManager()
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
                    if (errorMessage?.contains("no user record", ignoreCase = true) == true) {
                        showAlertDialog(this, "Erro de Login", "Email ou senha incorretos ou Usuário não cadastrado.")
                    } else {
                        showAlertDialog(this, "Erro de Login", "Email ou senha incorretos ou Usuário não cadastrado.")
                    }
                }
            }
        }
    }

    // Configuração do WorkManager

    private fun configureWorkManager() {
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "NotificationWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    // Solicitar permissão para notificações
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            } else {
                scheduleNotificationWorker()
            }
        } else {
            scheduleNotificationWorker()
        }
    }

    // Callback para lidar com a resposta do usuário
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Verifica se a permissão foi concedida
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissão para notificações ativada", Toast.LENGTH_SHORT).show()
            } else {
                // Permissão negada, mostre uma mensagem ou lide com isso adequadamente
                Toast.makeText(this, "Permissão para notificações negada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Configuração do WorkManager
    private fun scheduleNotificationWorker() {
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "NotificationWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    // Exibir alerta
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
}
