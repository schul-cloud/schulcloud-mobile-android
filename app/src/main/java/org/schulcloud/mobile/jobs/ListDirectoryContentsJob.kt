package org.schulcloud.mobile.jobs

import android.util.Log
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Sync
import org.schulcloud.mobile.models.file.Directory
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.network.ApiService
import ru.gildor.coroutines.retrofit.awaitResponse

class ListDirectoryContentsJob(private val path: String, callback: RequestJobCallback) : RequestJob(callback) {
    companion object {
        val TAG: String = ListDirectoryContentsJob::class.java.simpleName
    }

    override suspend fun onRun() {
        val response = ApiService.getInstance().listDirectoryContents(path).awaitResponse()

        if (response.isSuccessful) {
            if (BuildConfig.DEBUG) Log.i(TAG, "Contents for path $path received")

            // Sync
            Sync.Data.with(File::class.java, response.body()!!.files!!).run()
            Sync.Data.with(Directory::class.java, response.body()!!.directories!!).run()

        } else {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error while fetching contents for path $path")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }
}
