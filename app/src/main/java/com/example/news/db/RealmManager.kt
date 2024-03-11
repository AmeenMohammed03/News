package com.example.news.db

import com.example.news.models.Article
import io.realm.Realm

object RealmManager {

    fun saveHeadlines(countryCode: String, headlines: List<Article>) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransactionAsync { realm ->
            // Delete existing headlines for the given country code
            realm.where(Headlines::class.java)
                .equalTo("countryCode", countryCode)
                .findAll()
                .deleteAllFromRealm()

            // Save new headlines
            headlines.forEach { article ->
                val realmArticle = realm.createObject(Headlines::class.java)
                realmArticle.id = article.id ?: 0
                realmArticle.author = article.author
                realmArticle.content = article.content
                realmArticle.description = article.description
                realmArticle.publishedAt = article.publishedAt
                realmArticle.source = article.source
                realmArticle.title = article.title
                realmArticle.url = article.url
                realmArticle.urlToImage = article.urlToImage
                realmArticle.countryCode = countryCode
            }
        }
    }

    fun getHeadlines(countryCode: String): List<Article> {
        val realm = Realm.getDefaultInstance()
        val realmHeadlines = realm.where(Headlines::class.java)
            .equalTo("countryCode", countryCode)
            .findAll()

        // Convert Realm objects to Article objects
        return realm.copyFromRealm(realmHeadlines).map { realmArticle ->
            Article(
                realmArticle.id,
                realmArticle.author,
                realmArticle.content,
                realmArticle.description,
                realmArticle.publishedAt,
                realmArticle.source,
                realmArticle.title,
                realmArticle.url,
                realmArticle.urlToImage
            )
        }
    }
}
