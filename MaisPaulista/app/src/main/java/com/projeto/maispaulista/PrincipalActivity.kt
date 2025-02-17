package com.projeto.maispaulista

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.InputType
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.projeto.maispaulista.utils.BlogAdapter
import com.projeto.maispaulista.model.Blog
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.projeto.maispaulista.repository.BlogRepository
import com.projeto.maispaulista.service.BlogService
import com.projeto.maispaulista.utils.AuthUtils
import com.projeto.maispaulista.utils.ConsultaUtils
import com.projeto.maispaulista.utils.NetworkUtils
import com.projeto.maispaulista.utils.Variaveis
import kotlinx.coroutines.launch

class PrincipalActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var blogAdapter: BlogAdapter
    private lateinit var blogService: BlogService
    private var recyclerViewState: Parcelable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        AuthUtils.checkAuthentication(this)


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

        val buttonAcessar = findViewById<Button>(R.id.button_acessar)
        buttonAcessar.setOnClickListener {
            // Salva o estado do RecyclerView antes de abrir o link
            recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()

            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.paulista.pe.gov.br/2024/dinamico/")
            startActivity(intent)
        }


        if (!NetworkUtils.isNetworkAvailable(this)) {
            NetworkUtils.showNoNetworkDialog(this)
            setupRecyclerView()
        }

        setupRecyclerView()
        setupClickListeners()
        setupBottomNavigation()


    }



    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.blog_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val db = FirebaseFirestore.getInstance()
        val repository = BlogRepository(db)
        blogService = BlogService(repository)
2
        val query = blogService.getBlogQuery()
        val options = FirestoreRecyclerOptions.Builder<Blog>()
            .setQuery(query, Blog::class.java)
            .build()

        blogAdapter = BlogAdapter(this, options)
        recyclerView.adapter = blogAdapter
    }

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

    override fun onResume() {
        super.onResume()
        if (recyclerViewState != null) {
            recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
        }
    }

    override fun onPause() {
        super.onPause()
        recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
    }

    override fun onStart() {
        super.onStart()
        if (NetworkUtils.isNetworkAvailable(this)) {
            blogAdapter.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        blogAdapter.stopListening()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
        outState.putParcelable("RECYCLER_VIEW_STATE", recyclerViewState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val recyclerViewState = savedInstanceState.getParcelable<Parcelable>("RECYCLER_VIEW_STATE")
        recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

}