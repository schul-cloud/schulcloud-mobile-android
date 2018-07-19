package org.schulcloud.mobile.models.base

import android.arch.lifecycle.LiveData
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmModel
import io.realm.RealmResults


class LiveRealmData<T : RealmModel>(private val results: RealmResults<T>) : LiveData<List<T>>() {
    companion object {
        private val realm: Realm by lazy {
            Realm.getDefaultInstance()
        }
    }

    private val listener = RealmChangeListener<RealmResults<T>> { results ->
        if (results.isLoaded)
            value = if (results.isLoaded) realm.copyFromRealm(results) else emptyList()
    }

    override fun onActive() {
        results.addChangeListener(listener)
    }

    override fun onInactive() {
        results.removeChangeListener(listener)
    }
}
