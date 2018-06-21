package org.schulcloud.mobile.models.homework

import android.arch.lifecycle.LiveData
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import org.schulcloud.mobile.models.base.LiveRealmData
import org.schulcloud.mobile.utils.asLiveData
import java.text.SimpleDateFormat
import java.util.*

class HomeworkDao(private val realm: Realm){

    fun listHomework() : LiveRealmData<Homework>{
        return realm.where(Homework::class.java)
                .sort("dueDate", Sort.ASCENDING)
                .findAllAsync()
                .asLiveData()
    }

}