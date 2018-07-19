package org.schulcloud.mobile.models.base

import android.arch.lifecycle.LiveData
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmObject

/**
 * Date: 6/9/2018
 */
class RealmObjectLiveData<T : RealmObject>(private val result: T) : LiveData<T?>() {
    companion object {
        private val realm: Realm by lazy {
            Realm.getDefaultInstance()
        }
    }

    private val listener = RealmChangeListener<T> { result ->
        if (result.isLoaded)
            value = if (result.isLoaded) realm.copyFromRealm(result) else null
    }

    override fun onActive() {
        result.addChangeListener(listener)
    }

    override fun onInactive() {
        result.removeChangeListener(listener)
    }
}
