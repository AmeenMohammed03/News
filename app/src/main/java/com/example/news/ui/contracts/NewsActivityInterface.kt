package com.example.news.ui.contracts

interface NewsActivityInterface {
    fun getSelectedCountryCode(): String

    fun setLastUpdatedTime(time: String)

    fun isNetworkAvailable(): Boolean

}