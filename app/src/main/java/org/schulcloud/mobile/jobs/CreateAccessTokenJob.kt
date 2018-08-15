package org.schulcloud.mobile.jobs

import android.util.Log
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.config.Config
import org.schulcloud.mobile.utils.JWTUtil
import org.schulcloud.mobile.network.ApiService
import org.schulcloud.mobile.models.Credentials
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.storages.UserStorage
import ru.gildor.coroutines.retrofit.awaitResponse
import org.schulcloud.mobile.jobs.base.RequestJobCallback

class CreateAccessTokenJob(private val credentials: Credentials, callback: RequestJobCallback) : RequestJob(callback) {

    companion object {
        val TAG: String = CreateAccessTokenJob::class.java.simpleName
    }

    override suspend fun onRun() {
        val response = ApiService.getInstance().createToken(credentials).awaitResponse()
        val token = response.body()

        if (response.isSuccessful && token != null) {
            if (BuildConfig.DEBUG) Log.i(TAG, "AccessToken created")

            val userStorage = UserStorage()
            userStorage.accessToken = token.accessToken!!
            userStorage.userId = JWTUtil().decodeToCurrentUser(token.accessToken!!)!!

            callback?.success()
        } else {
            if (BuildConfig.DEBUG) Log.w(TAG, "AccessToken not created")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }
}
