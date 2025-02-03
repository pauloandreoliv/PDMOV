package com.projeto.maispaulista.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.projeto.maispaulista.R
import com.projeto.maispaulista.model.Blog
import com.squareup.picasso.Picasso

class BlogAdapter(private var blogList: List<Blog>) : RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    class BlogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.blog_image)
        val titleView: TextView = itemView.findViewById(R.id.blog_title)
        val descriptionView: TextView = itemView.findViewById(R.id.blog_description)
        val linkView: ImageView = itemView.findViewById(R.id.blog_link)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_blog, parent, false)
        return BlogViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blog = blogList[position]
        Picasso.get().load(blog.imageUrl).into(holder.imageView)
        holder.titleView.text = blog.title
        holder.descriptionView.text = blog.description
        holder.linkView.setOnClickListener {
            // Abra o link
        }
    }

    override fun getItemCount(): Int {
        return blogList.size
    }

    fun updateBlogs(blogs: List<Blog>) {
        this.blogList = blogs
        notifyDataSetChanged() // Atualiza o RecyclerView
    }
}