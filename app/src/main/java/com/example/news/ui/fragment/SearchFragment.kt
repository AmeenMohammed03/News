package com.example.news.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.news.R
import com.example.news.adapters.NewsAdapter
import com.example.news.api.Constants
import com.example.news.databinding.FragmentSearchBinding
import com.example.news.models.NewsResponse
import com.example.news.ui.NewsActivity
import com.example.news.viewModel.NewsViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var newsViewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var binding: FragmentSearchBinding


    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false
    private var searchJob: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)

        // Initialize ViewModel
        newsViewModel = (requireActivity() as NewsActivity).newsViewModel

        // Initialize RecyclerView
        setupSearchRecycler()

        // Observe search news results
        observeSearchNews()

        initViews()
    }

    private fun initViews() {
        // Set up item click listener for search results
        newsAdapter.setOnItemClickListener { article ->
            try {
                val articleFragment = ArticleFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable("article", article)
                    }
                }
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.webView, articleFragment)
                    .addToBackStack(null)
                    .commit()
                println("Navigation to article fragment from search successful")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Navigation to article fragment from search failed: ${e.message}")
                Toast.makeText(requireContext(), "Navigation failed", Toast.LENGTH_SHORT).show()
            }
        }

        // Initialize search edit text listener
        initSearchListener()

        // Initialize swipe-to-refresh listener
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }
    }

    private fun initSearchListener() {
        binding.searchEdit.addTextChangedListener { text ->
            searchJob?.cancel()
            searchJob = MainScope().launch {
                delay(Constants.SEARCH_NEWS_TIME_DELAY)
                text?.let {
                    if (text.toString().isNotEmpty()) {
                        isLoading = true
                        isLastPage = false
                        // Perform search when text is not empty
                        newsViewModel.searchNews(text.toString())
                    } else {
                        newsAdapter.differ.submitList(emptyList())
                        isLoading = false
                        isLastPage = true
                    }
                }
            }
        }
    }

    private fun refreshData() {
        // If isLoading is true, it means a search operation is already in progress, so just return
        if (isLoading) {
            return
        }
        // Trigger a new search with the current search query
        newsViewModel.searchNews(binding.searchEdit.text.toString())
    }

    private fun observeSearchNews() {
        newsViewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            showLoadingIndicator(false)

            handleSearchNewsResponse(response)
        })
    }

    private fun handleSearchNewsResponse(newsResponse: NewsResponse) {
        newsAdapter.differ.submitList(newsResponse.articles.toList())
        // Calculate total pages for pagination
        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
        isLastPage = newsViewModel.searchNewsPage == totalPages
        // Adjust padding if it's the last page
        if (isLastPage) {
            binding.recyclerSearch.setPadding(0, 0, 0, 0)
        }
        if (binding.swipeRefreshLayout.isRefreshing) {
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun showLoadingIndicator(show: Boolean) {
        if (show) {
            binding.paginationProgressBar.visibility = View.VISIBLE
            isLoading = true
        } else {
            binding.paginationProgressBar.visibility = View.INVISIBLE
            isLoading = false
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            // Check if it's not loading, not the last page, and reached the end of the list
            val isAtEndOfList =
                !isLoading && !isLastPage &&
                        (visibleItemCount + firstVisibleItemPosition >= totalItemCount) &&
                        firstVisibleItemPosition >= 0
            if (isAtEndOfList && !isScrolling) {
                // Trigger pagination
                newsViewModel.searchNews(binding.searchEdit.text.toString())
                isScrolling = true
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private fun setupSearchRecycler() {
        newsAdapter = NewsAdapter()
        binding.recyclerSearch.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(scrollListener)
        }
    }

    private fun clearSearchResults() {
        binding.searchEdit.text.clear()
        newsAdapter.differ.submitList(emptyList())
        isLoading = false
        isLastPage = false
        refreshData()
    }

    override fun onResume() {
        super.onResume()
        clearSearchResults()
    }
}