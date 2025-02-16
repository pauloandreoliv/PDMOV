package com.projeto.maispaulista.utils

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import com.projeto.maispaulista.R
import com.projeto.maispaulista.model.Blog

class BlogAdapter(private val context: Context, options: FirestoreRecyclerOptions<Blog>) :
    FirestoreRecyclerAdapter<Blog, BlogAdapter.BlogViewHolder>(options) {

        //referências
    class BlogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.blog_image)
        val titleView: TextView = itemView.findViewById(R.id.blog_title)
        val descriptionView: TextView = itemView.findViewById(R.id.blog_description)
        val linkView: ImageView = itemView.findViewById(R.id.blog_link)
    }

    //cria novos ViewHolders
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_blog, parent, false)
        Log.d("BlogAdapter", "onCreateViewHolder called")
        return BlogViewHolder(view)
    }

    //vincula dados a um ViewHolder existente
    override fun onBindViewHolder(holder: BlogViewHolder, position: Int, model: Blog) {
        Log.d("BlogAdapter", "onBindViewHolder called for position $position with blog title ${model.title}")
        Log.d("BlogAdapter", "Blog details - Image URL: ${model.imageUrl}, Description: ${model.description}, Link: ${model.link}")

        // Carregar imagem do drawable
        val drawableId = context.resources.getIdentifier(model.imageUrl.split(".")[0], "drawable", context.packageName)
        holder.imageView.setImageResource(drawableId)

        // Definir título e descrição
        holder.titleView.text = model.title
        holder.titleView.setTextColor(Color.BLACK)
        holder.descriptionView.text = model.description
        holder.descriptionView.setTextColor(Color.BLACK)

        // // Ação ao clicar no link
        val clickListener = View.OnClickListener {
            Log.d("BlogAdapter", "Navigating to link: ${model.link}")
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(model.link))
            context.startActivity(intent)
        }

        holder.titleView.setOnClickListener(clickListener)
        holder.descriptionView.setOnClickListener(clickListener)
        holder.linkView.setOnClickListener(clickListener)
    }

    //Utualiza os dados
    override fun onDataChanged() {
        super.onDataChanged()
        Log.d("BlogAdapter", "Data changed")
    }


    override fun onError(e: FirebaseFirestoreException) {
        super.onError(e)
        Log.e("BlogAdapter", "Error: ${e.message}")
    }
}