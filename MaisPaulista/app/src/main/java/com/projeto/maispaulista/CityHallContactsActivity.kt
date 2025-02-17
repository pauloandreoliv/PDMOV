package com.projeto.maispaulista


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.projeto.maispaulista.model.Contato
import com.projeto.maispaulista.service.ContatoService
import com.projeto.maispaulista.utils.AuthUtils
import com.projeto.maispaulista.utils.NetworkUtils
import com.projeto.maispaulista.utils.Variaveis

class CityHallContactsActivity : AppCompatActivity() {

    private lateinit var containerLayout: LinearLayout
    private val contatoService = ContatoService()
    private val REQUEST_CALL_PHONE_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_hall_contacts)
        AuthUtils.checkAuthentication(this)

        containerLayout = findViewById(R.id.containerLayout)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.white)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        // Solicitar permissões
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL_PHONE_PERMISSION)
        }

        // Configuração do botão de voltar
        val backArrow = findViewById<ImageView>(R.id.backArrow)
        backArrow.setOnClickListener {
            val intent = Intent(this, PrincipalActivity::class.java)
            startActivity(intent)
            finish()
        }

        loadContatos()
        setupBottomNavigation()
    }


    // Carregar dados do contato
    private fun loadContatos() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            NetworkUtils.showNoNetworkDialog(this)
            return
        }
        val currentUser = Variaveis.uid
        if (currentUser == null) {
            Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
            FirebaseAuth.getInstance().signOut()
            Variaveis.uid = null
            return startActivity(Intent(this, MainActivity::class.java))
        }
        contatoService.getContatos { contatos ->
            contatos.forEach { contato ->
                addContatoView(contato)
            }
        }
    }

    // Configuração do botão de voltar
    override fun onBackPressed() {
        val intent = Intent(this, PrincipalActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Adicionar contato na tela
    private fun addContatoView(contato: Contato) {
        val contatoLayout = LayoutInflater.from(this).inflate(R.layout.item_contato, containerLayout, false)

        val setorTextView = contatoLayout.findViewById<TextView>(R.id.setorTextView)
        val contatoTextView = contatoLayout.findViewById<TextView>(R.id.contatoTextView)
        val icCell = contatoLayout.findViewById<ImageView>(R.id.ic_cell)
        val icMsg = contatoLayout.findViewById<ImageView>(R.id.ic_msg)
        val icWpp = contatoLayout.findViewById<ImageView>(R.id.ic_wpp)

        setorTextView.text = contato.setor
        contatoTextView.text = "Telefone: ${contato.telefone} \nWhatsApp: ${contato.whatsapp} \nE-mail: ${contato.email}"

        // Configuração do botão de telefone
        icCell.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contato.telefone}"))
                startActivity(intent)
            } else {
                Toast.makeText(this, "Permissão para fazer chamadas não concedida.", Toast.LENGTH_SHORT).show()
            }
        }

        // Configuração do botão de e-mail
        icMsg.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(contato.email))
                putExtra(Intent.EXTRA_SUBJECT, "Escreva o Assunto aqui")
            }

            val chooser = Intent.createChooser(intent, "Escolha um aplicativo de email")
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(chooser)
            } else {
                Toast.makeText(this, "Nenhum aplicativo de email encontrado.", Toast.LENGTH_SHORT).show()
            }
        }


        // Configuração do botão de WhatsApp
        icWpp.setOnClickListener {
            val url = "https://api.whatsapp.com/send?phone=${contato.whatsapp}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        containerLayout.addView(contatoLayout)
    }

    // Barra de navegação
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
