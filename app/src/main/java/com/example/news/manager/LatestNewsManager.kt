package com.example.news.manager

import com.example.news.models.Article
import com.example.news.models.NewsResponse
import com.example.news.ui.contracts.LatestNewsFragmentInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class LatestNewsManager(private var view: LatestNewsFragmentInterface) {

    fun getLatestNews(countryCode: String) {
        if (view.isNetworkAvailable()) {
            view.getLatestNews(countryCode)
        } else {
            view.hideProgressBar()
            view.showNoNetworkDialog()
        }
    }

    fun handleResponse(response: Response<NewsResponse>) {
        if (response.isSuccessful) {
            response.body()?.let { newsResponse ->
                val articles = newsResponse.articles
                articles.removeAll { it.source!!.name.equals("[Removed]", true)}
                view.submitListToAdapter(articles)
            }
        } else {
            view.hideProgressBar()
            view.showInternalErrorDialog()
        }
    }
}