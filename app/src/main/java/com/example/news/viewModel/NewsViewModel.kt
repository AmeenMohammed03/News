package com.example.news.viewModel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.news.models.CountriesList
import com.example.news.models.NewsResponse
import com.example.news.repository.NewsRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(app: Application, private val newsRepository: NewsRepository) : AndroidViewModel(app) {

    val headlines: MutableLiveData<NewsResponse> = MutableLiveData()
    var headlinesPage = 1
    private var headlinesResponse: NewsResponse? = null

    val searchNews: MutableLiveData<NewsResponse> = MutableLiveData()
    var searchNewsPage = 1
    private var searchNewsResponse: NewsResponse? = null
    private var newSearchQuery: String? = null
    private var oldSearchQuery: String? = null

    private val selectedCountryCode: String = CountriesList.CountryList.DEFAULT_COUNTRY_CODE

    init {
        getHeadlines(selectedCountryCode)
    }

    fun getHeadlines(countryCode: String) = viewModelScope.launch {
        fetchNewsInternet(countryCode, false)
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        fetchNewsInternet(searchQuery, true)
    }

    private fun handleResponse(response: Response<NewsResponse>, isSearch: Boolean) {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if (isSearch) {
                    if (searchNewsResponse == null || newSearchQuery != oldSearchQuery) {
                        searchNewsPage = 1
                        oldSearchQuery = newSearchQuery
                        searchNewsResponse = resultResponse
                    } else {
                        searchNewsPage++
                        searchNewsResponse?.articles?.addAll(resultResponse.articles)
                    }
                    searchNews.postValue(searchNewsResponse)
                } else {
                    headlinesPage++
                    if (headlinesResponse == null) {
                        headlinesResponse = resultResponse
                    } else {
                        headlinesResponse?.articles?.addAll(resultResponse.articles)
                    }
                    filterRemovedArticles(headlinesResponse)
                    headlines.postValue(headlinesResponse)
                }
            }
        } else {
            // Handle error here
        }
    }

    private fun filterRemovedArticles(response: NewsResponse?) {
        response?.articles?.removeAll { it.source?.name == "[Removed]" }
    }

    private fun internetConnection(context: Context): Boolean {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            return getNetworkCapabilities(activeNetwork)?.run {
                hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            } ?: false
        }
    }

    private suspend fun fetchNewsInternet(query: String, isSearch: Boolean) {
        val loadingLiveData = if (isSearch) searchNews else headlines
        loadingLiveData.postValue(null)

        try {
            if (internetConnection(getApplication())) {
                val response = if (isSearch) {
                    newsRepository.searchNews(query, "202-02-29", "publishedAt") // Provide default values for 'from' and 'sortBy'
                } else {
                    newsRepository.getHeadlines(query)
                }
                handleResponse(response, isSearch)
            } else {
                loadingLiveData.postValue(null) // Handle no internet connection
            }
        } catch (t: Throwable) {
            loadingLiveData.postValue(null) // Handle error
        }
    }
}