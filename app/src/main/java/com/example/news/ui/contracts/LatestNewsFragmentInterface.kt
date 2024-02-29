package com.example.news.ui.contracts

import com.example.news.models.Article

interface LatestNewsFragmentInterface {

    fun initUi()

    fun getLatestNews(countryCode: String)

    fun showProgressBar()

    fun hideProgressBar()

    fun showNoNetworkDialog()

    fun showInternalErrorDialog()

    fun isNetworkAvailable(): Boolean

    fun submitListToAdapter(articles: List<Article>)


}