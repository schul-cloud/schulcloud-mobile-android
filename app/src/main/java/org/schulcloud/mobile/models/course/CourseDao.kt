package org.schulcloud.mobile.models.course

import android.arch.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.utils.asLiveData
import org.schulcloud.mobile.utils.firstAsLiveData

class CourseDao(private val realm: Realm) {
    fun courses(): LiveData<List<Course>> {
        return realm.where(Course::class.java)
                .findAllAsync()
                .asLiveData()
    }
    fun course(id: String): LiveData<Course?> {
        return realm.where(Course::class.java)
                .equalTo("id", id)
                .firstAsLiveData()
    }
}
