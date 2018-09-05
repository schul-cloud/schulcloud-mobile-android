package org.schulcloud.mobile.models.user

import android.util.Log
import androidx.lifecycle.LiveData
import io.realm.Realm
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import org.schulcloud.mobile.jobs.CreateAccessTokenJob
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Credentials
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.event.EventRepository
import org.schulcloud.mobile.models.homework.HomeworkRepository
import org.schulcloud.mobile.models.news.NewsRepository
import org.schulcloud.mobile.storages.UserStorage
import org.schulcloud.mobile.utils.userDao


object UserRepository {
    val TAG: String = UserRepository::class.java.simpleName

    @JvmStatic
    val token: String?
        get() = UserStorage.accessToken

    @JvmStatic
    val userId: String?
        get() = UserStorage.userId

    @JvmStatic
    val isAuthorized: Boolean
        get() = UserStorage.accessToken != null

    fun currentUser(realm: Realm): LiveData<User?> {
        return realm.userDao().user(userId ?: "")
    }

    fun login(email: String, password: String, callback: RequestJobCallback) {
        launch {
            // Login
            withContext(DefaultDispatcher) {
                CreateAccessTokenJob(Credentials(email, password), callback).run()
            }

            // Sync data in background
            syncCurrentUser()
            EventRepository.syncEvents()
            HomeworkRepository.syncHomeworkList()
            CourseRepository.syncCourses()
            NewsRepository.syncNews()
        }
    }

    fun logout() {
        UserStorage.clear()

        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { it.deleteAll() }
        realm.close()
    }

    suspend fun syncCurrentUser() {
        UserStorage.userId?.also {
            RequestJob.SingleData.with(it, { getUser(it) }).run()
        } ?: Log.w(TAG, "Request to sync current user while not signed in")
    }
}
