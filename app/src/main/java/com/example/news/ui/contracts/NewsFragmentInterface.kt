package com.example.news.ui.contracts

import com.example.news.db.NewsData
import com.example.news.models.Article

interface NewsFragmentInterface {

    fun initUi()

    fun getLatestNews(countryCode: String)

    fun showProgressBar()

    fun hideProgressBar()

    fun showNoNetworkDialog()

    fun showInternalErrorDialog()

    fun submitListToAdapter(articles: List<Article>)

    fun saveDataInRoom(data: NewsData)

    fun showNewsOnView(countryCode: String)

//    fun getSearchNews(searchQuery: String, from: String, sortBy: String)

}

interface SearchNewsFragmentInterface {

    fun initUi()

    fun searchForNews(query: String)

    fun showProgressBar()

    fun hideProgressBar()

    fun showNoNetworkDialog()

    fun showInternalErrorDialog()

    fun submitListToAdapter(articles: List<Article>)

    fun showNoNewsFoundToast()

    fun showErrorText()

    fun hideErrorText()

}