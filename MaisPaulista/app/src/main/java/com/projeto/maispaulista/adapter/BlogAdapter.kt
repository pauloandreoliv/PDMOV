package com.projeto.maispaulista.adapter

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
import com.squareup.picasso.Picasso

class BlogAdapter(options: FirestoreRecyclerOptions<Blog>) :
    FirestoreRecyclerAdapter<Blog, BlogAdapter.BlogViewHolder>(options) {

    class BlogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.blog_image)
        val titleView: TextView = itemView.findViewById(R.id.blog_title)
        val descriptionView: TextView = itemView.findViewById(R.id.blog_description)
        val linkView: ImageView = itemView.findViewById(R.id.blog_link)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_blog, parent, false)
        Log.d("BlogAdapter", "onCreateViewHolder called")
        return BlogViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int, model: Blog) {
        Log.d("BlogAdapter", "onBindViewHolder called for position $position with blog title ${model.title}")
        Log.d("BlogAdapter", "Blog details - Image URL: ${model.imageUrl}, Description: ${model.description}, Link: ${model.link}")

        Picasso.get().load(model.imageUrl).into(holder.imageView)
        holder.titleView.text = model.title
        holder.descriptionView.text = model.description
        holder.linkView.setOnClickListener {
            Log.d("BlogAdapter", "Link clicked for blog: ${model.title}")
            // Ação ao clicar no link
        }
    }

    override fun onDataChanged() {
        super.onDataChanged()
        Log.d("BlogAdapter", "Data changed")
    }

    override fun onError(e: FirebaseFirestoreException) {
        super.onError(e)
        Log.e("BlogAdapter", "Error: ${e.message}")
    }
}