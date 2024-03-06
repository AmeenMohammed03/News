package com.example.news.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.news.R
import com.example.news.models.Article


class NewsAdapter(private val onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    private val articles: MutableList<Article> = mutableListOf()

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val articleImage = itemView.findViewById<ImageView>(R.id.articleImage)
        val articleSource = itemView.findViewById<TextView>(R.id.articleSource)
        val articleTitle = itemView.findViewById<TextView>(R.id.articleTitle)
        val articleDescription = itemView.findViewById<TextView>(R.id.articleDescription)
        val articleDateTime = itemView.findViewById<TextView>(R.id.articleDateTime)
        val textViewNewsLink = itemView.findViewById<TextView>(R.id.textViewNewsLink)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return ArticleViewHolder(view)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.itemView.apply {
            if (article.urlToImage.isNullOrEmpty()) {
                Glide.with(this).load(R.drawable.placeholder_image).into(holder.articleImage)
            } else {
                Glide.with(this).load(article.urlToImage).into(holder.articleImage)
            }
            holder.articleSource.text = article.source?.name
            holder.articleTitle.text = article.title
            holder.articleDescription.text = article.description
            holder.articleDateTime.text = article.publishedAt
            holder.textViewNewsLink.text = "Read more..."

            holder.textViewNewsLink.setOnClickListener {
                onItemClickListener.onItemClick(article)
            }
        }
    }

    fun clearList() {
        articles.clear()
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(article: Article)
    }
}
