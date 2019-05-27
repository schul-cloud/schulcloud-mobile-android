@file:Suppress("TooManyFunctions")

package org.schulcloud.mobile.utils

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.schulcloud.mobile.BuildConfig.API_URL
import org.schulcloud.mobile.R
import org.schulcloud.mobile.models.user.UserRepository
import java.io.IOException

const val HEADER_COOKIE = "cookie"
const val HEADER_CONTENT_TYPE = "content-type"
const val HEADER_CONTENT_ENCODING = "content-encoding"

const val MIME_TEXT_PLAIN = "text/plain"
const val MIME_TEXT_HTML = "text/html"
const val MIME_APPLICATION_JSON = "application/json"

const val ENCODING_UTF_8 = "utf-8"

val HOST_API = API_URL.substringAfter("://").substringBefore(':')
val HOST = HOST_API.substringAfter('.')

val HTTP_CLIENT: OkHttpClient by lazy {
    OkHttpClient.Builder().addInterceptor { chain ->
        val builder = chain.request().newBuilder()
        if (UserRepository.isAuthorized)
            builder.addHeader(HEADER_COOKIE, "jwt=" + UserRepository.token)
        chain.proceed(builder.build())
    }.build()
}

fun String?.asUri(): Uri {
    return if (this == null)
        Uri.EMPTY
    else
        Uri.parse(this)
}

fun Context.prepareCustomTab(): CustomTabsIntent {
    return CustomTabsIntent.Builder().apply {
        setToolbarColor(ContextCompat.getColor(this@prepareCustomTab, R.color.brand_primary))
        setCloseButtonIcon(
                ContextCompat.getDrawable(this@prepareCustomTab,
                        R.drawable.ic_arrow_back_white_24dp)!!.asBitmap())
        addDefaultShareMenuItem()
        setStartAnimations(this@prepareCustomTab, R.anim.slide_in_right, R.anim.slide_out_left)
        setExitAnimations(this@prepareCustomTab, R.anim.slide_in_left, R.anim.slide_out_right)
    }.build()
}

fun Context.openUrl(url: Uri) {
    Log.i(TAG, "Opening url: $url")
    prepareCustomTab().launchUrl(this, url)
}

suspend fun resolveRedirect(url: String): Uri? = withContext(Dispatchers.IO) {
    if (url[0] != '/')
        return@withContext url.asUri()

    return@withContext try {
        val request = Request.Builder().url(combinePath(API_URL, url)).build()
        HTTP_CLIENT.newCall(request).execute()
                .request().url().toString().asUri()
    } catch (e: IOException) {
        Log.w(TAG, "Error resolving internal redirect", e)
        null
    }
}
