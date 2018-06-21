package org.schulcloud.mobile.models.topic

import io.realm.Realm
import org.schulcloud.mobile.models.base.LiveRealmData
import org.schulcloud.mobile.models.base.RealmObjectLiveData
import org.schulcloud.mobile.utils.asLiveData

/**
 * Date: 6/10/2018
 */
class TopicDao(private val realm: Realm) {

    fun topicsForCourse(courseId: String): LiveRealmData<Topic> {
        return realm.where(Topic::class.java)
                .equalTo("courseId", courseId)
                .sort("position")
                .findAllAsync()
                .asLiveData()
    }

    fun topic(id: String): RealmObjectLiveData<Topic> {
        return realm.where(Topic::class.java)
                .equalTo("id", id)
                .findFirstAsync()
                .asLiveData()
    }
}