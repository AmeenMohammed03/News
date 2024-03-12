package com.example.news.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NewsDao {

    @Insert
    suspend fun insertNewsData(newsData: NewsData)

    @Query("SELECT * FROM news_data")
    suspend fun getNewsData(): NewsData

    @Query("DELETE FROM news_data")
    suspend fun deleteNewsData()
}