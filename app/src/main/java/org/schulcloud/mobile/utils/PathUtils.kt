@file:Suppress("TooManyFunctions")
package org.schulcloud.mobile.utils

import android.os.Environment
import android.text.TextUtils
import android.util.Log
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


private const val TAG = "PathUtils"

fun String.getPathParts(limit: Int = 0): List<String> = trimSlashes().split(File.separator, limit = limit)

fun combinePath(vararg parts: String?): String = parts.toList().combinePath()
fun List<String?>.combinePath(): String {
    val builder = StringBuilder(this[0] ?: "")
    for (i in 1 until size) {
        if (this[i] == null || TextUtils.isEmpty(this[i]))
            continue

        val endsWithSeparator = builder.isNotEmpty() && builder[builder.length - 1] == File.separatorChar
        val beginsWithSeparator = this[i]!!.isNotEmpty() && this[i]!![0] == File.separatorChar

        if (endsWithSeparator && beginsWithSeparator)
            builder.append(this[i]!!.substring(1))
        else if (!endsWithSeparator && !beginsWithSeparator)
            builder.append(File.separatorChar).append(this[i])
        else
            builder.append(this[i])
    }
    return builder.toString()
}

val String.parentDirectory: String
    get() = trimTrailingSlash().substringBeforeLast(File.separatorChar).ensureTrailingSlash()
val String.fileExtension: String
    get() = substringAfterLast('.', "")

fun String.trimLeadingSlash(): String = if (length > 0 && this[0] == File.separatorChar) substring(1) else this
fun String.trimTrailingSlash(): String = if (length > 1 && this[length - 1] == File.separatorChar) substring(0, length - 1) else this
fun String.trimSlashes(): String = trimLeadingSlash().trimTrailingSlash()

fun String.ensureLeadingSlash(): String = if (length == 0 || this[0] != File.separatorChar) File.separator + this else this
fun String.ensureTrailingSlash(): String = if (length == 0 || this[length - 1] != File.separatorChar) this + File.separator else this
fun String.ensureSlashes(leading: Boolean = true, trailing: Boolean = false): String {
    return when (leading to trailing) {
        true to true -> ensureLeadingSlash().ensureTrailingSlash()
        true to false -> ensureLeadingSlash().trimTrailingSlash()
        false to true -> trimLeadingSlash().ensureTrailingSlash()
        false to false -> trimLeadingSlash().trimTrailingSlash()
        else -> throw AssertionError("All possible cases were handled")
    }
}

fun ResponseBody.writeToDisk(fileName: String): Boolean {
    val downloadDirPath = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .absolutePath
    try {
        val file = File(combinePath(downloadDirPath, fileName))
        if (!(file.exists() || file.createNewFile()))
            return false

        val outputStream = FileOutputStream(file)
        byteStream().copyTo(outputStream)
        outputStream.close()
        return true
    } catch (e: IOException) {
        Log.w(TAG, "ResponseBody.writeToDisk", e)
        return false
    }
}
