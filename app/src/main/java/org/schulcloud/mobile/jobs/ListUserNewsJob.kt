package org.schulcloud.mobile.jobs

import android.util.Log
import io.realm.Realm
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.news.News
import org.schulcloud.mobile.network.ApiService
import ru.gildor.coroutines.retrofit.awaitResponse

class ListUserNewsJob (callback: RequestJobCallback): RequestJob(callback) {

    companion object {
        val TAG: String = ListUserNewsJob::class.java.simpleName
    }

    override suspend fun onRun() {

        val response = ApiService.getInstance().listUserNews().awaitResponse();
        if (response.isSuccessful){

            if (BuildConfig.DEBUG)
                Log.i(TAG, "News recieved")

            //save news
            val recievedNews = response.body()!!.data!!

            val realm = Realm.getDefaultInstance()
            realm.executeTransaction{
                for (news in recievedNews){
                    realm.copyToRealmOrUpdate(news)
                }

            }
            realm.close()
        }
        else {
            if (BuildConfig.DEBUG)
                Log.e(TAG, "Error while fetching news list")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }

    }
}