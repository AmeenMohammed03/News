package com.example.news.db

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [NewsData::class], version = 1)
abstract class NewsDataBase: RoomDatabase() {
    abstract fun newsDao(): NewsDao
}