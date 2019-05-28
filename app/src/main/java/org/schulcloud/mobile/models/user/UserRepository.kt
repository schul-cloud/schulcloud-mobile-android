package org.schulcloud.mobile.models.user

import android.util.Log
import androidx.lifecycle.LiveData
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.schulcloud.mobile.jobs.CreateAccessTokenJob
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Credentials
import org.schulcloud.mobile.models.base.Repository
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.event.EventRepository
import org.schulcloud.mobile.models.homework.HomeworkRepository
import org.schulcloud.mobile.models.material.MaterialRepository
import org.schulcloud.mobile.models.news.NewsRepository
import org.schulcloud.mobile.storages.UserStorage
import org.schulcloud.mobile.utils.userDao


object UserRepository : Repository() {
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
        return user(realm, userId ?: "")
    }

    fun user(realm: Realm, id: String): LiveData<User?> {
        return realm.userDao().user(id)
    }


    fun login(email: String, password: String, callback: RequestJobCallback) {
        launch(Dispatchers.Default) {
            // Login
            withContext(Dispatchers.Default) {
                CreateAccessTokenJob(Credentials(email, password), callback).run()
            }

            // Sync data in background
            syncCurrentUser()
            EventRepository.syncEvents()
            HomeworkRepository.syncHomeworkList()
            CourseRepository.syncCourses()
            NewsRepository.syncNews()
            MaterialRepository.syncMaterials()
        }
    }

    fun logout() {
        UserStorage.clear()

        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { it.deleteAll() }
        realm.close()
    }


    suspend fun syncCurrentUser() {
        UserStorage.userId?.also { syncUser(it) }
                ?: Log.w(TAG, "Request to sync current user while not signed in")
    }

    suspend fun syncUser(id: String) {
        RequestJob.SingleData.with(id, { getUser(id) }).run()
    }
}
