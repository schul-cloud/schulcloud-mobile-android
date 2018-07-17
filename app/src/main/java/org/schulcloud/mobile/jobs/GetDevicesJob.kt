package org.schulcloud.mobile.jobs

import android.util.Log
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Sync
import org.schulcloud.mobile.models.notifications.Device
import org.schulcloud.mobile.network.ApiService
import ru.gildor.coroutines.retrofit.awaitResponse

class GetDevicesJob(callback: RequestJobCallback): RequestJob(callback){
    companion object {
        val TAG: String = GetDevicesJob::class.java.simpleName
    }

    override suspend fun onRun() {
        val response = ApiService.getInstance().getDevices().awaitResponse()

        if (response.isSuccessful) {
            if (BuildConfig.DEBUG) Log.i(TAG, "Devices received")

            // Sync
            Sync.Data.with(Device::class.java, response.body()!!.data!!).run()

        } else {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error while fetching devices")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }
}