package org.schulcloud.mobile.models.course

import androidx.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.models.base.Repository
import org.schulcloud.mobile.utils.courseDao


object CourseRepository : Repository() {
    fun courses(realm: Realm): LiveData<List<Course>> {
        return realm.courseDao().courses()
    }

    fun course(realm: Realm, id: String): LiveData<Course?> {
        return realm.courseDao().course(id)
    }


    suspend fun syncCourses() {
        RequestJob.Data.with({ listUserCourses() }).run()
    }

    suspend fun syncCourse(id: String) {
        RequestJob.SingleData.with(id, { getCourse(id) }).run()
    }
}
