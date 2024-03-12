package com.example.news.manager

import com.example.news.db.NewsData
import com.example.news.models.Article
import com.example.news.models.NewsResponse
import com.example.news.ui.contracts.NewsFragmentInterface
import com.example.news.ui.contracts.SearchNewsFragmentInterface
import com.google.gson.Gson
import retrofit2.Response

class NewsManager() {

    private lateinit var view: NewsFragmentInterface
    private lateinit var searchView: SearchNewsFragmentInterface
    constructor(view: NewsFragmentInterface) : this() {
        this.view = view
    }

    constructor(view: SearchNewsFragmentInterface) : this() {
        this.searchView = view
    }

    fun getLatestNews(countryCode: String, newsData: NewsData) {
        if (newsData == null ||
            (System.currentTimeMillis() - newsData.timestamp > 1000 * 60 * 30
                    && view.isNetworkAvailable())) {
            view.getLatestNews(countryCode)
        } else {
            val articles = Gson().fromJson(newsData.value, Array<Article>::class.java).toList()
            view.submitListToAdapter(articles)
        }
    }

    fun handleLatestNewsResponse(response: Response<NewsResponse>) {
        if (response.isSuccessful) {
            response.body()?.let { newsResponse ->
                val articles = newsResponse.articles
                articles.removeAll { it.source!!.name.equals("[Removed]", true)}
                view.submitListToAdapter(articles)
                view.saveDataInRoom(NewsData("latest", toJsonString(articles)))
            }
        } else {
            view.hideProgressBar()
            view.showInternalErrorDialog()
        }
    }

    fun searchForNews(query: String) {
        if (query.length >= 3) {
            searchView.showProgressBar()
            searchView.hideErrorText()
            searchView.searchForNews(query)
        } else {
            searchView.showErrorText()
        }
    }

    fun filterNews(newsJson: String, searchQuery: String): List<Article> {
        val articles = Gson().fromJson(newsJson, Array<Article>::class.java).toList()
        return articles.filter { it.title.contains(searchQuery, true) }
    }

    private fun toJsonString(obj: Any) : String = Gson().toJson(obj)
}