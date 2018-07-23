package org.schulcloud.mobile.jobs

import android.util.Log
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Sync
import org.schulcloud.mobile.models.homework.Homework
import org.schulcloud.mobile.network.ApiService
import ru.gildor.coroutines.retrofit.awaitResponse

class GetHomeworkJob(private val homeworkId: String, callback: RequestJobCallback) : RequestJob(callback) {
    companion object {
        val TAG: String = GetCourseJob::class.java.simpleName
    }

    override suspend fun onRun() {
        val response = ApiService.getInstance().getHomework(homeworkId).awaitResponse()

        if (response.isSuccessful) {
            if (BuildConfig.DEBUG) Log.i(TAG, "Homework ${homeworkId}Id received")

            // Sync
            Sync.SingleData.with(Homework::class.java, response.body()!!).run()

        } else {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error while fetching homework $homeworkId")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }
}