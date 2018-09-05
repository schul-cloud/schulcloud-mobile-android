package org.schulcloud.mobile.models.news

import androidx.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.utils.newsDao


object NewsRepository {
    fun newsList(realm: Realm): LiveData<List<News>> {
        return realm.newsDao().listNews()
    }

    fun news(realm: Realm, id: String): LiveData<News?> {
        return realm.newsDao().news(id)
    }


    suspend fun syncNews() {
        RequestJob.Data.with({ listUserNews() }).run()
    }

    suspend fun syncNews(id: String) {
        RequestJob.SingleData.with(id, { getNews(id) }).run()
    }
}
