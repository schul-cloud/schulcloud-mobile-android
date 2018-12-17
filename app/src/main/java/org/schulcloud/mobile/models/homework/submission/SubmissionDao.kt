package org.schulcloud.mobile.models.homework.submission

import androidx.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.utils.allAsLiveData
import org.schulcloud.mobile.utils.firstAsLiveData

class SubmissionDao(private val realm: Realm) {

    fun submissionsForHomework(homeworkId: String): LiveData<List<Submission>> {
        return realm.where(Submission::class.java)
                .equalTo("homeworkId", homeworkId)
                .allAsLiveData()
    }

    fun submission(id: String): LiveData<Submission?> {
        return realm.where(Submission::class.java)
                .equalTo("id", id)
                .firstAsLiveData()
    }

    fun submission(homeworkId: String, studentId: String): LiveData<Submission?> {
        return realm.where(Submission::class.java)
                .equalTo("homeworkId", homeworkId)
                .equalTo("studentId", studentId)
                .firstAsLiveData()
    }


    fun updateSubmission(value: Submission) {
        realm.executeTransaction {
            it.copyToRealmOrUpdate(value)
        }
    }
}
