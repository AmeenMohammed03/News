package com.example.news.db

import android.util.Log
import io.realm.Realm
object RealmManager {
    fun saveHeadline(headline: Headlines) {
        val realm = Realm.getDefaultInstance()
        try {
            realm.executeTransaction { realm ->
                realm.copyToRealmOrUpdate(headline)
            }
            Log.d("RealmManager", "Headline saved successfully: $headline")
        } catch (e: Exception) {
            Log.e("RealmManager", "Error saving headline: $headline", e)
        } finally {
            realm.close()
        }
    }

    fun getAllHeadlines(): List<Headlines> {
        val realm = Realm.getDefaultInstance()
        val headlines = realm.where(Headlines::class.java).findAll()
        val headlinesList = realm.copyFromRealm(headlines)
        realm.close()
        return headlinesList
    }

    fun deleteAllHeadlines() {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { realm ->
            realm.deleteAll()
        }
        realm.close()
    }
}
