package com.example.news.api

import com.example.news.api.Constants.Companion.API_KEY
import com.example.news.models.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {
    @GET("v2/top-headlines")
    suspend fun getHeadlines(
        @Query("country")
        countryCode: String,
        @Query("apikey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        searchQuery: String,
        @Query("from")
        from: String,
        @Query("sortBy")
        sortBy: String,
        @Query("apikey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>
}