package org.schulcloud.mobile.models.homework.submission

import androidx.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.utils.submissionDao

object SubmissionRepository {
    fun submissionsForHomework(realm: Realm, homeworkId: String): LiveData<List<Submission>> {
        return realm.submissionDao().submissionsForHomework(homeworkId)
    }

    fun submission(realm: Realm, id: String): LiveData<Submission?> {
        return realm.submissionDao().submission(id)
    }

    fun submission(realm: Realm, homeworkId: String, studentId: String): LiveData<Submission?> {
        return realm.submissionDao().submission(homeworkId, studentId)
    }


    suspend fun updateSubmission(realm: Realm, value: Submission) {
        realm.submissionDao().updateSubmission(value)
        RequestJob.UpdateSingleData.with(value, { updateSubmission(value.id, value) }).run()
    }


    suspend fun syncSubmissionsForHomework(homeworkId: String) {
        RequestJob.Data.with({ listHomeworkSubmissions(homeworkId) },
                { equalTo("homeworkId", homeworkId) }).run()
    }

    suspend fun syncSubmission(id: String) {
        RequestJob.SingleData.with(id, { getSubmission(id) }).run()
    }
}
