package org.schulcloud.mobile.models.topic

import android.arch.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.jobs.GetTopicJob
import org.schulcloud.mobile.jobs.ListCourseTopicsJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.utils.topicDao

/**
 * Date: 6/10/2018
 */
object TopicRepository {

    fun topicsForCourse(realm: Realm, courseId: String): LiveData<List<Topic>> {
        return realm.topicDao().topicsForCourse(courseId)
    }

    fun topic(realm: Realm, id: String): LiveData<Topic?> {
        return realm.topicDao().topic(id)
    }

    suspend fun syncTopics(courseId: String) {
        ListCourseTopicsJob(courseId, object : RequestJobCallback() {
            override fun onSuccess() {
            }

            override fun onError(code: ErrorCode) {
            }
        }).run()
    }

    suspend fun syncTopic(topicId: String) {
        GetTopicJob(topicId, object : RequestJobCallback() {
            override fun onSuccess() {
            }

            override fun onError(code: ErrorCode) {
            }
        }).run()
    }
}
