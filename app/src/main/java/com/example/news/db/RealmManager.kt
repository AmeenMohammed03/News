package com.example.news.db

import com.example.news.models.Article
import io.realm.Realm

object RealmManager {

    fun saveHeadlines(countryCode: String, headlines: List<Article>) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { realm ->
            val oldHeadlines = realm.where(Headlines::class.java).equalTo("countryCode", countryCode).findAll()
            oldHeadlines.deleteAllFromRealm()

            headlines.forEach { article ->
                val realmArticle = realm.createObject(Headlines::class.java, article)
                realmArticle.id = article.id
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
        realm.close()
    }

    fun getHeadlines(countryCode: String): MutableList<Article> {
        val realm = Realm.getDefaultInstance()
        val realmHeadlines = realm.where(Headlines::class.java).equalTo("countryCode", countryCode).findAll()
        val articles = realmHeadlines.map { realmArticle ->
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
        realm.close()
        return articles.toMutableList()
    }
}
