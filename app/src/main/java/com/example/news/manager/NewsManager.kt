package com.example.news.manager

import com.example.news.db.RealmData
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

    fun getLatestNews(countryCode: String) {
        if (view.isNetworkAvailable()) {
            view.getLatestNews(countryCode)
        } else {
            view.hideProgressBar()
            view.showNoNetworkDialog()
        }
    }

    fun handleLatestNewsResponse(response: Response<NewsResponse>) {
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

    fun searchForNews(query: String) {
        if (searchView.isNetworkAvailable()) {
            searchView.searchForNews(query)
        } else {
            searchView.hideProgressBar()
            searchView.showNoNetworkDialog()
        }
    }

    fun handleSearchNewsResponse(response: Response<NewsResponse>) {
        if (response.isSuccessful) {
            response.body()?.let { newsResponse ->
                val articles = newsResponse.articles
                articles.removeAll { it.source!!.name.equals("[Removed]", true)}
                if (articles.isEmpty()) {
                    searchView.showNoNewsFoundToast()
                }
                searchView.submitListToAdapter(articles)
            }
        } else {
            searchView.hideProgressBar()
            searchView.showInternalErrorDialog()
        }
    }

    private fun toJsonString(obj: Any) : String = Gson().toJson(obj)

    fun fromJsonString(json: String, classOfT: Class<RealmData>) : RealmData = Gson().fromJson(json, classOfT)
}