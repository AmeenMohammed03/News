package com.example.news.ui

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.news.R
import com.example.news.ui.fragment.SearchFragment

class NewsActivity : AppCompatActivity() {
    val newsViewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        // Initialize your views
        val searchButton = findViewById<LinearLayout>(R.id.search_button)
        searchButton.setOnClickListener {
            // Handle the click event of the search button
            // Open the SearchFragment
            val fragment = SearchFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }
}