package com.example.news.ui

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.news.R
import com.example.news.manager.NewsManager
import com.example.news.models.CountriesList
import com.example.news.ui.contracts.NewsActivityInterface
import com.example.news.ui.fragment.LatestNewsFragment
import com.example.news.ui.fragment.SearchNewsFragment
import com.google.android.material.navigation.NavigationView
import io.realm.Realm
import io.realm.RealmConfiguration
import java.util.Locale

class NewsActivity : AppCompatActivity(), NewsActivityInterface {
    private var fm: FragmentManager? = null
    private var fragment: Fragment? = null
    private var selectedCountryCode: String = "us"

    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var searchIcon: ImageView
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: LinearLayout
    private lateinit var latestNewsButton: LinearLayout
    private lateinit var latestUpdatedTextView: TextView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        fm = supportFragmentManager
        fragment = fm!!.findFragmentById(R.id.news_container_view)
        fragment = LatestNewsFragment()
        val ft = fm!!.beginTransaction()
        ft.replace(R.id.news_container_view, fragment!!)
        ft.commitAllowingStateLoss()

        initViews()
        setUpNavigationDrawer()
        updateLastUpdatedTime()
    }

    private fun initViews() {
        drawerLayout = findViewById(R.id.drawerlayout)
        navigationView = findViewById(R.id.navigationView)
        searchIcon = findViewById(R.id.search_icon)
        latestNewsButton = findViewById(R.id.latest_news_button)
        searchButton = findViewById(R.id.search_button)
        searchEditText = findViewById(R.id.search_edit_text)
        latestUpdatedTextView = findViewById(R.id.last_updated_time)
        toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawerlayout)

        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        searchButton.setOnClickListener {
            val fragment = SearchNewsFragment()
            println("Search button clicked")
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.news_container_view, fragment)
                addToBackStack(null)
                commit()
            }
        }

        latestNewsButton.setOnClickListener {
            searchEditText.visibility = View.GONE
            searchEditText.text.clear()
            latestUpdatedTextView.visibility = View.VISIBLE
            val latestNewsFragment = LatestNewsFragment()
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.news_container_view, latestNewsFragment)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                commit()
            }
        }

    }
    private fun setUpNavigationDrawer() {
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.nav_open, R.string.nav_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        findViewById<NavigationView>(R.id.navigationView).setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_select_country -> {
                    showCountrySearchDialog()
                    true
                }
                R.id.menu_exit -> {
                    finish() // Close the activity
                    true
                }
                else -> false
            }
        }
    }

    private fun showCountrySearchDialog() {
        val countries = CountriesList.CountryList.countries.map { it.name }

        val dialogView = layoutInflater.inflate(R.layout.country_search_dialog, null)
        val editTextCountrySearch = dialogView.findViewById<EditText>(R.id.editTextCountrySearch)
        val listViewCountries = dialogView.findViewById<ListView>(R.id.listViewCountrySearch)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, countries)
        listViewCountries.adapter = adapter

        editTextCountrySearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.length >=3) adapter.filter.filter(s)
            }
        })

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()

        listViewCountries.setOnItemClickListener { adapterView, _, i, _ ->
            val selectedCountryName = adapterView.getItemAtPosition(i).toString()
            selectedCountryCode = CountriesList.getCountryCode(selectedCountryName).toString()
            dialog.dismiss()
            drawerLayout.closeDrawer(GravityCompat.START)
            val fragment = LatestNewsFragment()
            val ft = fm!!.beginTransaction()
            ft.replace(R.id.news_container_view, fragment)
            ft.commitAllowingStateLoss()
        }
        dialog.show()
    }

    private fun updateLastUpdatedTime() {
        val currentTime = System.currentTimeMillis()
        val formattedTime = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(currentTime)

        val lastUpdatedTextView = findViewById<TextView>(R.id.last_updated_time)
        lastUpdatedTextView.text = getString(R.string.last_updated, formattedTime)
        lastUpdatedTextView.visibility = View.VISIBLE
    }

    override fun getSelectedCountryCode(): String = selectedCountryCode
}