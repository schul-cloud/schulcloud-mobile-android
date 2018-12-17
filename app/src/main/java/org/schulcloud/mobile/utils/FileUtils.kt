@file:Suppress("TooManyFunctions")

package org.schulcloud.mobile.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.content.FileProvider
import org.schulcloud.mobile.R
import org.schulcloud.mobile.config.Config
import org.schulcloud.mobile.controllers.base.ContextAware
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.models.file.FileRepository
import org.schulcloud.mobile.models.file.SignedUrlRequest
import org.schulcloud.mobile.network.ApiService
import retrofit2.HttpException
import ru.gildor.coroutines.retrofit.await
import java.io.IOException
import java.io.InputStream
import java.io.File as JavaFile


private const val TAG = "FileUtils"

// General utilities
fun JavaFile.create(): Boolean {
    return try {
        if (!parentFile.exists() && !parentFile.mkdirs())
            return false
        else if (exists() && !delete())
            return false
        else if (!createNewFile())
            return false
        else true
    } catch (e: IOException) {
        Log.e(TAG, e.toString())
        false
    }
}

fun JavaFile.saveDelete(): Boolean {
    return try {
        !exists() || delete()
        true
    } catch (e: IOException) {
        false
    }
}


// File picking
suspend fun ContextAware.createFilePickerIntent(): Intent? {
    if (!requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
        currentContext.showGenericError(R.string.file_pick_error_readPermissionDenied)
        return null
    }

    return Intent(Intent.ACTION_GET_CONTENT).apply {
        type = "*/*"
        addCategory(Intent.CATEGORY_OPENABLE)
    }
}

fun Context.createTakePhotoIntent(): TakePhotoInfo? {
    // Create temp file used by camera app to store the photo
    val tempPicture = java.io.File(filesDir, combinePath("temp", "photo.jpg"))
    if (!tempPicture.create()) {
        showGenericError(R.string.file_pick_error_createTempFile)
        return null
    }

    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    val uri = FileProvider.getUriForFile(this, Config.FILE_PROVIDER, tempPicture)
    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)

    // Test if apps can handle the intent, i.e. a camera app is installed
    if (intent.resolveActivity(packageManager) == null) {
        showGenericError(R.string.file_pick_error_noCamera)
        return null
    }

    // Grant temporary write permission for the destination file to all resolved activities
    val intentActivities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
    for (resolveInfo in intentActivities)
        grantUriPermission(resolveInfo.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

    return TakePhotoInfo(intent, tempPicture, uri)
}

data class TakePhotoInfo(
    val intent: Intent,
    val tempFile: JavaFile,
    val tempFileUri: Uri
)


// Download
@Suppress("ComplexMethod")
suspend fun ContextAware.downloadFile(file: File, download: Boolean) {
    try {
        val response = ApiService.getInstance().generateSignedUrl(
                SignedUrlRequest().apply {
                    action = SignedUrlRequest.ACTION_GET
                    path = Uri.decode(file.key)
                    fileType = file.type
                }).await()

        if (download) {
            if (!requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                currentContext.showGenericError(R.string.file_fileDownload_error_savePermissionDenied)
                return
            }

            currentContext.withProgressDialog(R.string.file_fileDownload_progress) {
                val result = ApiService.getInstance().downloadFile(response.url!!).await()
                if (!result.writeToDisk(file.name.orEmpty())) {
                    currentContext.showGenericError(R.string.file_fileDownload_error_save)
                    return@withProgressDialog
                }
                currentContext.showGenericSuccess(
                        currentContext.getString(R.string.file_fileDownload_success, file.name))
            }
        } else {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(Uri.parse(response.url), response.header?.contentType)
            }
            val packageManager = currentContext.packageManager
            if (packageManager != null && intent.resolveActivity(packageManager) != null)
                currentContext.startActivity(intent)
            else
                currentContext.showGenericError(currentContext.getString(R.string.file_fileOpen_error_cantResolve,
                        file.name?.fileExtension))
        }
    } catch (e: HttpException) {
        @Suppress("MagicNumber")
        when (e.code()) {
            404 -> currentContext.showGenericError(R.string.file_fileOpen_error_404)
            else -> currentContext.showGenericError(R.string.file_fileOpen_error)
        }
    }
}


// Upload
fun Context.prepareFileRead(uri: Uri): FileReadInfo? {
    val (name, size) = uri.let {
        @Suppress("Recycle")
        contentResolver?.query(it, null, null, null, null)
    }?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        cursor.moveToFirst()
        return@use cursor.getString(nameIndex) to cursor.getLong(sizeIndex)
    } ?: return null

    return FileReadInfo(name, size) { contentResolver.openInputStream(uri) }
}

suspend fun Context.uploadFile(
    uri: Uri?,
    path: String = FileRepository.pathPersonal(),
    name: String? = null,
    addEnding: Boolean = true
): File? {
    val fileReadInfo = uri?.let { prepareFileRead(it) }
    if (fileReadInfo == null) {
        showGenericError(R.string.file_pick_error_read)
        return null
    }

    return withProgressDialog(R.string.file_fileUpload_progress) {
        val fileName = when {
            name == null -> fileReadInfo.name
            addEnding -> "$name.${fileReadInfo.name.fileExtension}"
            else -> name
        }
        val file = FileRepository.upload(path, fileName, fileReadInfo.size) {
            fileReadInfo.streamGenerator().also {
                if (it == null)
                    showGenericError(R.string.file_pick_error_read)
            }
        }
        if (file == null) {
            showGenericError(R.string.file_fileUpload_error_upload)
            return@withProgressDialog null
        } else showGenericSuccess(R.string.file_fileUpload_success)

        FileRepository.syncDirectory(path)
        return@withProgressDialog file
    }
}

data class FileReadInfo(
    val name: String,
    val size: Long,
    val streamGenerator: () -> InputStream?
)
