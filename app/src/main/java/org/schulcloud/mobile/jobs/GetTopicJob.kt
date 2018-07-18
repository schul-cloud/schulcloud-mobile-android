package org.schulcloud.mobile.jobs

import android.util.Log
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Sync
import org.schulcloud.mobile.models.topic.Topic
import org.schulcloud.mobile.network.ApiService
import ru.gildor.coroutines.retrofit.awaitResponse

class GetTopicJob(private val topicId: String, callback: RequestJobCallback) : RequestJob(callback) {
    companion object {
        val TAG: String = GetTopicJob::class.java.simpleName
    }

    override suspend fun onRun() {
        val response = ApiService.getInstance().getTopic(topicId).awaitResponse()

        if (response.isSuccessful) {
            if (BuildConfig.DEBUG) Log.i(TAG, "Topics $topicId received")

            // Sync
            Sync.SingleData.with(Topic::class.java, response.body()!!).run()

        } else {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error while fetching topic $topicId")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }
}