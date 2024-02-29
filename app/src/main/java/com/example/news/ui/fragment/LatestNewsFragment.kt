package com.example.news.ui.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.news.R
import com.example.news.api.RetrofitInstance
import com.example.news.models.Article
import com.example.news.models.CountriesList
import com.example.news.adapters.NewsAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LatestNewsFragment: Fragment(R.layout.fragment_latest_news), NewsAdapter.OnItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: NewsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_latest_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        recyclerView = requireView().findViewById(R.id.recyclerHeadlines)
        progressBar = requireView().findViewById(R.id.paginationProgressBar)
        adapter = NewsAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        getLatestNews(CountriesList.CountryList.DEFAULT_COUNTRY_CODE)

    }

    fun getLatestNews(countryCode: String) {
        if (internetConnection(requireContext())) {
            CoroutineScope(Dispatchers.IO).launch {
                val response = RetrofitInstance.api.getHeadlines(countryCode)
                if (response.isSuccessful) {
                    response.body()?.let { newsResponse ->
                        withContext(Dispatchers.Main) {
                            val articles = newsResponse.articles
                            articles.removeAll { it.source!!.name.equals("[Removed]", true)}
                            adapter.differ.submitList(articles)
                        }
                    }
                }
            }
        }
    }

    private fun internetConnection(context: Context): Boolean {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            return getNetworkCapabilities(activeNetwork)?.run {
                when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } ?: false
        }
    }

    override fun onItemClick(article: Article) {
        val bundle = Bundle()
        bundle.putString("url", article.url)
        val articleFragment = ArticleFragment()
        articleFragment.arguments = bundle
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.news_container_view, articleFragment)
            addToBackStack(null)
            commit()
        }
    }
}