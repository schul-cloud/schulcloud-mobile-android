package org.schulcloud.mobile.jobs.base

import android.util.Log
import io.realm.RealmModel
import io.realm.RealmQuery
import kotlinx.coroutines.experimental.launch
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.models.Sync
import org.schulcloud.mobile.models.base.HasId
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.network.ApiService
import org.schulcloud.mobile.network.ApiServiceInterface
import org.schulcloud.mobile.network.FeathersResponse
import org.schulcloud.mobile.utils.NetworkUtil
import org.schulcloud.mobile.utils.it
import retrofit2.Call
import ru.gildor.coroutines.retrofit.awaitResponse


abstract class RequestJob(
    protected val callback: RequestJobCallback?,
    private vararg val preconditions: Precondition
) {
    companion object {
        val TAG: String = RequestJob::class.java.simpleName
    }

    enum class Precondition {
        AUTH
    }

    @Suppress("TooGenericExceptionCaught")
    suspend fun run() {
        if (preconditions.contains(Precondition.AUTH) && !UserRepository.isAuthorized) {
            callback?.error(RequestJobCallback.ErrorCode.NO_AUTH)
            return
        }

        if (!NetworkUtil.isOnline()) {
            callback?.error(RequestJobCallback.ErrorCode.NO_NETWORK)
            return
        }

        launch {
            try {
                onRun()
            } catch (e: Throwable) {
                Log.w(TAG, "Error running job", e)
                callback?.error(RequestJobCallback.ErrorCode.ERROR)
            }
        }
    }

    protected abstract suspend fun onRun()


    @Suppress("SpreadOperator")
    class Data<T>(
        private val clazz: Class<T>,
        private val call: ApiServiceInterface.() -> Call<FeathersResponse<List<T>>>,
        private val toDelete: RealmQuery<T>.() -> RealmQuery<T>,
        callback: RequestJobCallback? = null,
        preconditions: Array<out Precondition>
    ) : RequestJob(callback, *preconditions) where T : RealmModel, T : HasId {
        companion object {
            private val TAG: String = SingleData::class.java.simpleName

            inline fun <reified T> with(
                noinline call: ApiServiceInterface.() -> Call<FeathersResponse<List<T>>>,
                noinline toDelete: RealmQuery<T>.() -> RealmQuery<T> = ::it,
                callback: RequestJobCallback? = null,
                vararg preconditions: Precondition
            ): Data<T> where T : RealmModel, T : HasId {
                return Data(T::class.java, call, toDelete, callback, preconditions)
            }
        }

        override suspend fun onRun() {
            val response = call(ApiService.getInstance()).awaitResponse()
            val data = response.body()?.data

            if (response.isSuccessful && data != null) {
                if (BuildConfig.DEBUG)
                    Log.i(TAG, "${data.size} ${clazz.simpleName}s received")

                // Sync
                Sync.Data.with(clazz, data, toDelete).run()
            } else {
                if (BuildConfig.DEBUG)
                    Log.e(TAG, "Error while fetching ${clazz.simpleName}s")
                callback?.error(RequestJobCallback.ErrorCode.ERROR)
            }
        }
    }

    @Suppress("SpreadOperator")
    class SingleData<T>(
        private val clazz: Class<T>,
        private val itemId: String,
        private val call: ApiServiceInterface.() -> Call<T>,
        callback: RequestJobCallback? = null,
        preconditions: Array<out Precondition>
    ) : RequestJob(callback, *preconditions) where T : RealmModel, T : HasId {
        companion object {
            private val TAG: String = SingleData::class.java.simpleName

            inline fun <reified T> with(
                itemId: String,
                noinline call: ApiServiceInterface.() -> Call<T>,
                callback: RequestJobCallback? = null,
                vararg preconditions: Precondition
            ): SingleData<T> where T : RealmModel, T : HasId {
                return SingleData(T::class.java, itemId, call, callback, preconditions)
            }
        }

        override suspend fun onRun() {
            val response = call(ApiService.getInstance()).awaitResponse()

            if (response.isSuccessful) {
                if (BuildConfig.DEBUG)
                    Log.i(TAG, "${clazz.simpleName} $itemId received")

                // Sync
                Sync.SingleData.with(clazz, response.body(), itemId).run()
            } else {
                if (BuildConfig.DEBUG)
                    Log.e(TAG, "Error while fetching ${clazz.simpleName} $itemId")
                callback?.error(RequestJobCallback.ErrorCode.ERROR)
            }
        }
    }


    @Suppress("SpreadOperator")
    class UpdateSingleData<T>(
        private val clazz: Class<T>,
        private val item: T,
        private val call: ApiServiceInterface.() -> Call<T>,
        callback: RequestJobCallback? = null,
        preconditions: Array<out Precondition>
    ) : RequestJob(callback, *preconditions) where T : RealmModel, T : HasId {
        companion object {
            private val TAG: String = SingleData::class.java.simpleName

            inline fun <reified T> with(
                item: T,
                noinline call: ApiServiceInterface.() -> Call<T>,
                callback: RequestJobCallback? = null,
                vararg preconditions: Precondition
            ): UpdateSingleData<T> where T : RealmModel, T : HasId {
                return UpdateSingleData(T::class.java, item, call, callback, preconditions)
            }
        }

        override suspend fun onRun() {
            val response = call(ApiService.getInstance()).awaitResponse()

            if (response.isSuccessful) {
                if (BuildConfig.DEBUG)
                    Log.i(TAG, "${clazz.simpleName} ${item.id} updated")

                // Sync
                Sync.SingleData.with(clazz, response.body(), item.id).run()
            } else {
                if (BuildConfig.DEBUG)
                    Log.e(TAG, "Error while updating ${clazz.simpleName}s")
                callback?.error(RequestJobCallback.ErrorCode.ERROR)
            }
        }
    }
}
