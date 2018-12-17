package org.schulcloud.mobile.models.homework

import androidx.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.utils.homeworkDao


object HomeworkRepository {
    fun homeworkList(realm: Realm): LiveData<List<Homework>> {
        return realm.homeworkDao().homeworkList()
    }

    fun openHomeworkForNextWeek(realm: Realm): LiveData<List<Homework>> {
        return realm.homeworkDao().openHomeworkForNextWeek()
    }

    fun homework(realm: Realm, id: String): LiveData<Homework?> {
        return realm.homeworkDao().homework(id)
    }
    fun homeworkBlocking(realm: Realm, id: String): Homework? {
        return realm.homeworkDao().homeworkBlocking(id)
    }


    suspend fun syncHomeworkList() {
        RequestJob.Data.with({ listUserHomework() }).run()
    }

    suspend fun syncHomework(id: String) {
        RequestJob.SingleData.with(id, { getHomework(id) }).run()
    }
}
