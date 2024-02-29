package com.example.news.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.news.R
import com.example.news.ui.fragment.LatestNewsFragment

class NewsActivity : AppCompatActivity() {
    private var fm: FragmentManager? = null
    private var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        fm = supportFragmentManager
        fragment = fm!!.findFragmentById(R.id.news_container_view)
        fragment = LatestNewsFragment()
        val ft = fm!!.beginTransaction()
        ft.replace(R.id.news_container_view, fragment!!)
        ft.commitAllowingStateLoss()
    }
}