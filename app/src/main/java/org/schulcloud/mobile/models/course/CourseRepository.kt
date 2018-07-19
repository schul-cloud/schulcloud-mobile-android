package org.schulcloud.mobile.models.course

import android.arch.lifecycle.LiveData
import io.realm.Realm
import kotlinx.coroutines.experimental.async
import org.schulcloud.mobile.jobs.GetCourseJob
import org.schulcloud.mobile.jobs.ListUserCoursesJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.utils.courseDao

object CourseRepository {
    init {
        async {
            syncCourses()
        }
    }

    fun courses(realm: Realm): LiveData<List<Course>> {
        return realm.courseDao().courses()
    }

    fun course(realm: Realm, id: String): LiveData<Course?> {
        return realm.courseDao().course(id)
    }

    suspend fun syncCourses() {
        ListUserCoursesJob(object : RequestJobCallback() {
            override fun onSuccess() {
            }

            override fun onError(code: ErrorCode) {
            }
        }).run()
    }

    suspend fun syncCourse(courseId: String) {
        GetCourseJob(courseId, object : RequestJobCallback() {
            override fun onSuccess() {
            }

            override fun onError(code: ErrorCode) {
            }
        }).run()
    }
}
