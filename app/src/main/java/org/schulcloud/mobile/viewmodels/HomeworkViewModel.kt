package org.schulcloud.mobile.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.realm.Realm
import io.realm.RealmResults
import org.schulcloud.mobile.models.homework.Homework
import org.schulcloud.mobile.models.homework.HomeworkRepository

class HomeworkViewModel (id: String) : ViewModel() {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    private var homework: LiveData<Homework> = HomeworkRepository.getHomeworkForId(realm, id)

    fun getHomework(): LiveData<Homework> {
        return homework
    }
}