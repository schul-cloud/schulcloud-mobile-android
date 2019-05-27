package org.schulcloud.mobile.models.topic

import androidx.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.models.base.Repository
import org.schulcloud.mobile.utils.topicDao

object TopicRepository : Repository() {

    fun topicsForCourse(realm: Realm, courseId: String): LiveData<List<Topic>> {
        return realm.topicDao().topicsForCourse(courseId)
    }

    fun topic(realm: Realm, id: String): LiveData<Topic?> {
        return realm.topicDao().topic(id)
    }


    suspend fun syncTopics(courseId: String) {
        RequestJob.Data.with({ listCourseTopics(courseId) },
                { equalTo("courseId", courseId) }).run()
    }

    suspend fun syncTopic(id: String) {
        RequestJob.SingleData.with(id, { getTopic(id) }).run()
    }
}
