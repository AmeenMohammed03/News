package com.example.news.manager

import com.example.news.db.NewsData
import com.example.news.models.Article
import com.example.news.models.NewsResponse
import com.example.news.ui.contracts.NewsActivityInterface
import com.example.news.ui.contracts.NewsFragmentInterface
import com.example.news.ui.contracts.SearchNewsFragmentInterface
import com.google.gson.Gson
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class NewsManager() {

    private lateinit var view: NewsFragmentInterface
    private lateinit var searchView: SearchNewsFragmentInterface
    private lateinit var activityCallBack: NewsActivityInterface
    constructor(view: NewsFragmentInterface) : this() {
        this.view = view
    }

    constructor(view: SearchNewsFragmentInterface) : this() {
        this.searchView = view
    }

    constructor(activityCallBack: NewsActivityInterface) : this() {
        this.activityCallBack = activityCallBack
    }

    fun setLatestNewsView(view: NewsFragmentInterface) {
        this.view = view
    }

    fun setSearchNewsView(view: SearchNewsFragmentInterface) {
        this.searchView = view
    }

    fun setActivityCallBack(activityCallBack: NewsActivityInterface) {
        this.activityCallBack = activityCallBack
    }

    fun getLatestNews(countryCode: String, newsData: NewsData?) {
        if (newsData == null || newsData.country != countryCode ||
            (System.currentTimeMillis() - newsData.timestamp > 1000 * 60 * 30
                    && activityCallBack.isNetworkAvailable())) {
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
                view.saveDataInRoom(NewsData("latest", toJsonString(articles), country = activityCallBack.getSelectedCountryCode()))
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

    fun setLastUpdatedTime(newsData: NewsData?) {
        val currentTime = System.currentTimeMillis()
        var formattedTime = android.icu.text.SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(currentTime)
        if (newsData == null || (System.currentTimeMillis() - newsData.timestamp > 1000 * 60 * 30)) {
            activityCallBack.setLastUpdatedTime(formattedTime)
        } else {
            formattedTime = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(newsData.timestamp)
            activityCallBack.setLastUpdatedTime(formattedTime)
        }
    }

    companion object {

        private var instance: NewsManager? = null
        private val TAG = NewsManager::class.java.simpleName
        fun getInstance(view: SearchNewsFragmentInterface): NewsManager {
            if (instance == null) {
                synchronized(NewsManager::class.java) {
                    if (instance == null) {
                        instance = NewsManager(view)
                    }
                }
            }
            return instance!!
        }
        fun getInstance(view: NewsFragmentInterface): NewsManager {
            if (instance == null) {
                synchronized(NewsManager::class.java) {
                    if (instance == null) {
                        instance = NewsManager(view)
                    }
                }
            }
            return instance!!
        }

        fun getInstance(activityCallBack: NewsActivityInterface): NewsManager {
            if (instance == null) {
                synchronized(NewsManager::class.java) {
                    if (instance == null) {
                        instance = NewsManager(activityCallBack)
                    }
                }
            }
            return instance!!
        }
    }
}