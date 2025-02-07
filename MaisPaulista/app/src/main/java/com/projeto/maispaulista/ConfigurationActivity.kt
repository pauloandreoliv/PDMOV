package com.projeto.maispaulista

import android.app.AlertDialog
import android.content.Context
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
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.projeto.maispaulista.model.User
import com.projeto.maispaulista.adapter.Variaveis
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

        nomeEdit = findViewById(R.id.nomeEdit)
        cpfEdit = findViewById(R.id.cpfEdit)
        emailTextView = findViewById(R.id.emailTextView)



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.configurationLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.seF