package org.schulcloud.mobile.utils

import android.arch.lifecycle.LiveData
import io.realm.*
import org.schulcloud.mobile.models.base.LiveRealmData
import org.schulcloud.mobile.models.base.RealmObjectLiveData
import org.schulcloud.mobile.models.content.ContentDao
import org.schulcloud.mobile.models.course.CourseDao
import org.schulcloud.mobile.models.file.FileDao
import org.schulcloud.mobile.models.topic.TopicDao


// Convenience extension on RealmResults to return as LiveRealmData
fun <T : RealmModel> RealmResults<T>.asLiveData(): LiveData<List<T>> = LiveRealmData(this)

// Convenience extension on RealmObject to return as RealmObjectLiveData
fun <T : RealmObject> T.asLiveData(): LiveData<T?> = RealmObjectLiveData(this)

fun <T : RealmObject> RealmQuery<T>.firstAsLiveData(): LiveData<T?> {
    return this.findAllAsync()
            .asLiveData()
            .map { it.getOrNull(0) }
}

fun Realm.courseDao(): CourseDao = CourseDao(this)
fun Realm.topicDao(): TopicDao = TopicDao(this)
fun Realm.contentDao(): ContentDao = ContentDao(this)

fun Realm.fileDao(): FileDao = FileDao(this)
