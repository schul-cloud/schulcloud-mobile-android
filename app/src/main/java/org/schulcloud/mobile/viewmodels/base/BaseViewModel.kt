package org.schulcloud.mobile.viewmodels.base

import android.util.Log
import androidx.lifecycle.ViewModel
import io.realm.Realm
import java.io.IOException

abstract class BaseViewModel : ViewModel() {
    companion object {
        private val TAG = BaseViewModel::class.simpleName
    }

    protected val realm by lazy { Realm.getDefaultInstance() }


    override fun onCleared() {
        try {
            realm.close()
        } catch (e: IOException) {
            Log.w(TAG, "Error closing realm instance")
        }

        super.onCleared()
    }
}
