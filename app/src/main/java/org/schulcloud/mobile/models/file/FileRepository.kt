package org.schulcloud.mobile.models.file

import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import io.realm.Realm
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.Okio
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.ListDirectoryContentsJob
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.network.ApiService
import org.schulcloud.mobile.utils.*
import ru.gildor.coroutines.retrofit.awaitResponse
import java.io.InputStream
import java.io.File as JavaFile

object FileRepository {
    private val TAG = FileRepository.javaClass.simpleName
    const val CONTEXT_MY = "my"
    const val CONTEXT_MY_API = "users"
    const val CONTEXT_COURSES = "courses"

    fun files(realm: Realm, path: String): LiveData<List<File>> {
        return realm.fileDao().files(path)
    }

    fun files(realm: Realm, ids: Array<String>): LiveData<List<File>> {
        return realm.fileDao().files(ids)
    }

    fun directories(realm: Realm, path: String): LiveData<List<Directory>> {
        return realm.fileDao().directories(path)
    }


    suspend fun updateFile(realm: Realm, value: File) {
        realm.fileDao().updateFile(value)
        RequestJob.UpdateSingleData.with(value, { updateFile(value.id, value) }).run()
    }


    suspend fun syncDirectory(path: String) {
        ListDirectoryContentsJob(path).run()
    }

    suspend fun syncFile(id: String) {
        RequestJob.SingleData.with(id, { getFile(id) }).run()
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


    /**
     * @param path Path on server including the file name; defaults to private folder.
     */
    suspend fun upload(path: String, name: String, size: Long, streamGenerator: () -> InputStream?): File? {
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(name.fileExtension)
        val mediaType = mimeType?.let { MediaType.parse(it) }

        // Generate URL for upload
        val signedUrlReq = SignedUrlRequest().also {
            it.action = SignedUrlRequest.ACTION_PUT
            it.path = combinePath(path, name)
            it.fileType = mimeType
        }
        val signedUrlRes = ApiService.getInstance().generateSignedUrl(signedUrlReq).awaitResponse()
        val body = signedUrlRes.body()
        val uploadUrl = body?.url
        val header = body?.header
        if (!signedUrlRes.isSuccessful || body == null || uploadUrl == null || header == null) {
            if (BuildConfig.DEBUG)
                Log.e(TAG, "upload $path: Error generating signed URL")
            return null
        }

        // Actual upload
        val uploadReq = object : RequestBody() {
            override fun contentType() = mediaType
            override fun contentLength() = size
            override fun writeTo(sink: BufferedSink?) {
                Okio.source(streamGenerator() ?: return).use {
                    sink?.writeAll(it)
                }
            }
        }
        val uploadRes = ApiService.getInstance().uploadFile(uploadUrl,
                header.contentType, header.metaPath, header.metaName, header.metaFlatName, header.metaThumbnail,
                uploadReq).awaitResponse()
        if (!uploadRes.isSuccessful) {
            if (BuildConfig.DEBUG)
                Log.e(TAG, "upload $path: Error uploading file")
            return null
        }

        // Notify SC server of new file
        val newFile = CreateFileRequest().also {
            it.key = combinePath(header.metaPath, Uri.encode(name))
            it.path = header.metaPath?.ensureTrailingSlash()
            it.name = name
            it.type = mimeType
            it.size = size
            it.flatFileName = header.metaFlatName
            it.thumbnail = header.metaThumbnail
        }
        val persistRes = ApiService.getInstance().persistFile(newFile).awaitResponse()
        val file = persistRes.body()
        if (!persistRes.isSuccessful || file == null) {
            if (BuildConfig.DEBUG)
                Log.e(TAG, "upload $path: Error persisting file")
            return null
        }
        return file
    }
}
