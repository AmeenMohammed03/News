package com.example.news.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_data")
data class NewsData(
    @PrimaryKey var key: String,
    var value: String,
    var timestamp: Long = System.currentTimeMillis(),
    var country: String = "us"
)