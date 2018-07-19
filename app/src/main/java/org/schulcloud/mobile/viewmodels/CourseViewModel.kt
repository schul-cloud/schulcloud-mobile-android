package org.schulcloud.mobile.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.realm.Realm
import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.topic.Topic
import org.schulcloud.mobile.models.topic.TopicRepository

/**
 * Date: 6/9/2018
 */
class CourseViewModel(id: String) : ViewModel() {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    val course: LiveData<Course?> = CourseRepository.course(realm, id)
    val topics: LiveData<List<Topic>> = TopicRepository.topicsForCourse(realm, id)
}
