package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.LiveData
import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.topic.Topic
import org.schulcloud.mobile.models.topic.TopicRepository
import org.schulcloud.mobile.viewmodels.base.BaseViewModel


class CourseViewModel(val id: String) : BaseViewModel() {
    val course: LiveData<Course?> = CourseRepository.course(realm, id)
    val topics: LiveData<List<Topic>> = TopicRepository.topicsForCourse(realm, id)
}
