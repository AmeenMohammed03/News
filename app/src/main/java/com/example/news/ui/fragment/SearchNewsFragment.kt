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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.news.R
import com.example.news.adapters.NewsAdapter
import com.example.news.manager.NewsManager
import com.example.news.models.Article
import com.example.news.repository.NewsRepository
import com.example.news.ui.DialogUtil
import com.example.news.ui.contracts.SearchNewsFragmentInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SearchNewsFragment : Fragment(R.layout.fragment_search), SearchNewsFragmentInterface, NewsAdapter.OnItemClickListener {

    private val TAG = "SearchNewsFragment"
    private lateinit var searchEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var manager: NewsManager
    private lateinit var newsRepository: NewsRepository
    private lateinit var adapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    override fun initUi(){
        searchEditText = requireView().findViewById(R.id.searchEdit)
        recyclerView = requireView().findViewById(R.id.recyclerView)
        manager = NewsManager(this)
        newsRepository = NewsRepository()
        adapter = NewsAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //do nothing
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.isNullOrEmpty()) {
                    adapter.clearList()
                } else if (p0.toString().length >= 3){
                    fetchPreviousMonthNews(p0.toString())
                    manager.searchForNews(p0.toString())
                }
            }

        })
    }

    override fun searchForNews(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentDate = LocalDateTime.now()
                val previousMonthDate = currentDate.minusMonths(1)
                val from = previousMonthDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val response = newsRepository.searchNews(query, from, "popularity")
                manager.handleSearchNewsResponse(response)
            } catch (e: Exception) {
                e.printStackTrace()
//                showInternalErrorDialog()
            }
        }
    }

    private fun fetchPreviousMonthNews(query: String) {
        val startOfPreviousMonth = getStartOfPreviousMonth()
        CoroutineScope(Dispatchers.IO).launch {
            val response = newsRepository.searchNews(query, startOfPreviousMonth, "popularity")
            manager.handleSearchNewsResponse(response)
        }
    }

    private fun getStartOfPreviousMonth(): String {
        val currentDate = LocalDate.now()
        val firstDayOfCurrentMonth = currentDate.withDayOfMonth(1)
        val startOfPreviousMonth = firstDayOfCurrentMonth.minusMonths(1)
        return startOfPreviousMonth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    override fun submitListToAdapter(articles: List<Article>){
        CoroutineScope(Dispatchers.Main).launch {
            adapter.differ.submitList(articles)
        }
    }

    override fun showProgressBar(){
        // show progress bar
    }

    override fun hideProgressBar(){
        // hide progress bar
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