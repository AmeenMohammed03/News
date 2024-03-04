package com.example.news.db

import com.example.news.models.Source
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Headlines (
    @PrimaryKey
    var id: Int? = null,
    var author: String,
    var content: String,
    var description: String,
    var publishedAt: String,
    var source: Source?,
    var title: String,
    var url: String,
    var urlToImage: String
):RealmObject()
