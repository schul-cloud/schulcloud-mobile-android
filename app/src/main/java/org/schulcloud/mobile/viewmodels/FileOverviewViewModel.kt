package org.schulcloud.mobile.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.realm.Realm
import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.models.course.CourseRepository

class FileOverviewViewModel : ViewModel() {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    val courses: LiveData<List<Course>> = CourseRepository.courses(realm)
}
