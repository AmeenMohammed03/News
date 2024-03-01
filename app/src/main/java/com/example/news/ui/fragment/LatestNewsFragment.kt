package com.example.news.ui.fragment

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.news.R
import com.example.news.adapters.NewsAdapter
import com.example.news.manager.NewsManager
import com.example.news.models.Article
import com.example.news.repository.NewsRepository
import com.example.news.ui.contracts.NewsFragmentInterface
import com.example.news.ui.contracts.NewsActivityInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LatestNewsFragment: Fragment(R.layout.fragment_latest_news), NewsAdapter.OnItemClickListener ,
    NewsFragmentInterface {
    private lateinit var recyclerView: RecyclerView
   // private lateinit var progressBar: ProgressBar
    private lateinit var adapter: NewsAdapter
    private lateinit var newsRepository: NewsRepository
    private lateinit var dialog: AlertDialog
    private lateinit var manager: NewsManager
    private lateinit var activityCallBack: NewsActivityInterface

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_latest_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    override fun initUi() {
        recyclerView = requireView().findViewById(R.id.recyclerHeadlines) as RecyclerView
       // progressBar = requireView().findViewById(R.id.paginationProgressBar)
        adapter = NewsAdapter(this)
        manager = NewsManager(this)
        activityCallBack = requireActivity() as NewsActivityInterface
        newsRepository = NewsRepository()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        val countryCode = activityCallBack.getSelectedCountryCode()
        manager.getLatestNews(countryCode)
    }

    override fun getLatestNews(countryCode: String) {
       // progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            manager.handleResponse(newsRepository.getHeadlines(countryCode))
        }
    }

    override fun submitListToAdapter(articles: List<Article>) {
        CoroutineScope(Dispatchers.Main).launch {
            adapter.differ.submitList(articles)
        }
    }

    override fun showNoNetworkDialog() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                dialog = AlertDialog.Builder(requireContext())
                    .setTitle("No Network")
                    .setMessage("Please check your internet connection")
                    .setPositiveButton("OK") { _, _ -> }
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun showInternalErrorDialog() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                dialog = AlertDialog.Builder(requireContext())
                    .setTitle("Internal Error")
                    .setMessage("An internal error occurred. Please try again later")
                    .setPositiveButton("OK") { _, _ -> }
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun showProgressBar() {
        //progressBar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
       // progressBar.visibility = View.INVISIBLE
    }

    override fun isNetworkAvailable(): Boolean {
        (requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
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