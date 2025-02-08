package com.projeto.maispaulista

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.projeto.maispaulista.adapter.BlogAdapter
import com.projeto.maispaulista.model.Blog
import android.util.Log
import androidx.core.content.ContextCompat
import com.projeto.maispaulista.adapter.Variaveis

class PrincipalActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var blogAdapter: BlogAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        Log.d("PrincipalActivity", "onCreate called")

        // Ajuste de margens para barra de status e navegação
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.statusBarColor = ContextCompat.getColor(this, android.R.color.white)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        // Inicializa componentes
        setupRecyclerView()
        setupClickListeners()
        setupBottomNavigation()
    }

    //Faz busca no Firestore e configura o RecyclerView

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.blog_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        Log.d("PrincipalActivity", "RecyclerView setup")

        val query = FirebaseFirestore.getInstance().collection("blogs")
        val options = FirestoreRecyclerOptions.Builder<Blog>()
            .setQuery(query, Blog::class.java)
            .build()
        Log.d("PrincipalActivity", "FirestoreRecyclerOptions built")

        blogAdapter = BlogAdapter(this, options)
        recyclerView.adapter = blogAdapter
        Log.d("PrincipalActivity", "Adapter set")
    }


    //logica de redirecionamentos
    private fun setupClickListeners() {
        val redirecionamentos = mapOf(
            R.id.registrarSolicitacao to Register_RequestsActivity::class.java,
            R.id.acompanharSolicitacao to MyRequestsActivity::class.java,
            R.id.MyConsultas to MyConsultationsActivity::class.java,
            R.id.agendarConsultas to ScheduleConsultationsActivity::class.java,
            R.id.perguntasFrequentes to FrequentQuestionActivity::class.java,
            R.id.contatos to CityHallContactsActivity::class.java
        )

        redirecionamentos.forEach { (viewId, activityClass) ->
            findViewById<View>(viewId).setOnClickListener {
                startActivity(Intent(this, activityClass))
            }
        }
    }

    //menu inferior
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
                    startActivity(Intent(this, MainActivity::class.java))
                    Variaveis.uid = ""
                    true
                }
                else -> false
            }
        }

    }

    override fun onBackPressed() {

        val intent = Intent(this,  MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    override fun onStart() {
        super.onStart()
        blogAdapter.startListening()
        Log.d("PrincipalActivity", "Adapter started listening")
    }

    override fun onStop() {
        super.onStop()
        blogAdapter.stopListening()
        Log.d("PrincipalActivity", "Adapter stopped listening")
    }
}

