package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.LiveData
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.topic.Topic
import org.schulcloud.mobile.models.topic.TopicRepository
import org.schulcloud.mobile.viewmodels.base.BaseViewModel


class TopicViewModel(val id: String) : BaseViewModel() {
    val topic: LiveData<Topic?> = TopicRepository.topic(realm, id)

    fun course(id: String) = CourseRepository.course(realm, id)
}
