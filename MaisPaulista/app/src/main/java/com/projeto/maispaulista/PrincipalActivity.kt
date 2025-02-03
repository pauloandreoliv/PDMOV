package com.projeto.maispaulista

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.projeto.maispaulista.adapter.BlogAdapter
import com.projeto.maispaulista.repository.BlogRepository
import com.projeto.maispaulista.view.BlogViewModel
import com.projeto.maispaulista.view.BlogViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch



class PrincipalActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var blogAdapter: BlogAdapter

    private val blogViewModel: BlogViewModel by viewModels {
        BlogViewModelFactory(BlogRepository()) // Instancia o ViewModel usando um Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        // Ajuste de margens para barra de status e navegação
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa componentes
        setupRecyclerView()
        setupClickListeners()
        setupBottomNavigation()
        setupOnBackPressed()

        // Observar blogs
        observeBlogs()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.blog_recycler_view) // Certifique-se que o ID corresponde ao do layout XML
        recyclerView.layoutManager = LinearLayoutManager(this)
        blogAdapter = BlogAdapter(emptyList())
        recyclerView.adapter = blogAdapter
    }

    private fun observeBlogs() {
        lifecycleScope.launch {
            blogViewModel.blogList.collectLatest { blogs ->
                blogAdapter.updateBlogs(blogs) // Atualiza o Adapter com os novos dados
            }
        }
    }

    private fun setupClickListeners() {
        val redirecionamentos = mapOf(
            R.id.registrarSolicitacao to RegisterResquestsActivity::class.java,
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
                    onBackPressedDispatcher.onBackPressed()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupOnBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else {
                    finish()
                }
            }
        })
    }
}


