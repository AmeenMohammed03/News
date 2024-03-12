package com.example.news.ui.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.news.R
import com.example.news.adapters.NewsAdapter
import com.example.news.db.NewsDataBase
import com.example.news.manager.NewsManager
import com.example.news.models.Article
import com.example.news.repository.NewsRepository
import com.example.news.ui.DialogUtil
import com.example.news.ui.contracts.SearchNewsFragmentInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(R.layout.fragment_search), SearchNewsFragmentInterface, NewsAdapter.OnItemClickListener {

    private val TAG = "SearchNewsFragment"
    private lateinit var searchEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var manager: NewsManager
    private lateinit var newsRepository: NewsRepository
    private lateinit var adapter: NewsAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var errorText: TextView
    private lateinit var progressBar: ProgressBar
    companion object {
        lateinit var dataBase: NewsDataBase
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataBase = Room.databaseBuilder(requireContext(), NewsDataBase::class.java, "news_data").build()
        initUi()
    }

    override fun initUi(){
        searchEditText = requireView().findViewById(R.id.searchEdit)
        recyclerView = requireView().findViewById(R.id.recyclerView)
        swipeRefreshLayout = requireView().findViewById(R.id.swipeRefreshLayout)
        errorText = requireView().findViewById(R.id.errorTextView)
        progressBar = requireView().findViewById(R.id.paginationProgressBar)
        manager = NewsManager.getInstance(this)
        manager.setSearchNewsView(this)
        newsRepository = NewsRepository()
        adapter = NewsAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        swipeRefreshLayout.setOnRefreshListener {
            val query = searchEditText.text.toString()
            manager.searchForNews(query)
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                manager.searchForNews(p0.toString())
            }
        })
    }

    override fun searchForNews(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val value = dataBase.newsDao().getNewsData().value
            val filteredArticles = manager.filterNews(value, query)
            submitListToAdapter(filteredArticles)

        }
    }

    override fun submitListToAdapter(articles: List<Article>){
        CoroutineScope(Dispatchers.Main).launch {
            adapter.differ.submitList(articles)
            swipeRefreshLayout.isRefreshing = false
            progressBar.visibility = View.GONE
        }
    }

    override fun showProgressBar(){
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgressBar(){
        progressBar.visibility = View.GONE
    }

    override fun showErrorText(){
        errorText.visibility = View.VISIBLE
    }

    override fun hideErrorText(){
        errorText.visibility = View.GONE
    }

    override fun showNoNetworkDialog() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                DialogUtil.showDialog(
                    requireContext(),
                    "No Network",
                    "Please check your internet connection and try again"
                ).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun showInternalErrorDialog(){
        CoroutineScope(Dispatchers.Main).launch {
            try {
                DialogUtil.showDialog(
                    requireContext(),
                    "Internal Error",
                    "An internal error occurred. Please try again later"
                ).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun showNoNewsFoundToast() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                Toast.makeText(requireContext(), "No news found", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
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