package org.schulcloud.mobile.models.file

import android.arch.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.jobs.ListDirectoryContentsJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.utils.*

object FileRepository {
    const val CONTEXT_MY = "my"
    const val CONTEXT_MY_API = "users"
    const val CONTEXT_COURSES = "courses"

    fun files(realm: Realm, path: String): LiveData<List<File>> {
        return realm.fileDao().files(path)
    }

    fun directories(realm: Realm, path: String): LiveData<List<Directory>> {
        return realm.fileDao().directories(path)
    }


    suspend fun syncDirectory(path: String) {
        ListDirectoryContentsJob(path, object : RequestJobCallback() {
            override fun onSuccess() {
            }

            override fun onError(code: ErrorCode) {
            }
        }).run()
    }


    fun fixPath(path: String): String {
        var fixedPath = path.trimLeadingSlash().ensureTrailingSlash()
        if (path.startsWith(CONTEXT_MY))
            fixedPath = path.replaceRange(0, CONTEXT_MY.length, pathPersonal("").trimTrailingSlash())
        return fixedPath
    }

    fun pathPersonal(path: String? = null): String {
        return combinePath(CONTEXT_MY_API, UserRepository.userId!!, path)
    }

    fun pathCourse(courseId: String, path: String? = null): String {
        return combinePath(CONTEXT_COURSES, courseId, path)
    }
}
