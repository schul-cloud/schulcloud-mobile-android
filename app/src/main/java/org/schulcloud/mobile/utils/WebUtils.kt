package org.schulcloud.mobile.utils

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.schulcloud.mobile.BuildConfig.API_URL
import org.schulcloud.mobile.R
import org.schulcloud.mobile.models.user.UserRepository
import java.io.IOException


/**
 * Date: 6/11/2018
 */
const val HEADER_COOKIE = "cookie"
const val HEADER_CONTENT_TYPE = "content-type"
const val HEADER_CONTENT_ENCODING = "content-encoding";

const val MIME_TEXT_PLAIN = "text/plain"
const val MIME_TEXT_HTML = "text/html"
const val MIME_APPLICATION_JSON = "application/json"

const val ENCODING_UTF_8 = "utf-8"

val HOST = API_URL.substringBeforeLast(":")


val HTTP_CLIENT: OkHttpClient by lazy {
    OkHttpClient.Builder().addInterceptor { chain ->
        val builder = chain.request().newBuilder()
        if (UserRepository.isAuthorized)
            builder.addHeader(HEADER_COOKIE, "jwt=" + UserRepository.token);
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
        setCloseButtonIcon(ContextCompat.getDrawable(this@prepareCustomTab, R.drawable.ic_arrow_back_white_24dp)!!.asBitmap())
        addDefaultShareMenuItem()
        setStartAnimations(this@prepareCustomTab, R.anim.slide_in_right, R.anim.slide_out_left)
        setExitAnimations(this@prepareCustomTab, R.anim.slide_in_left, R.anim.slide_out_right)
    }.build()
}

fun Context.openUrl(url: Uri) {
    logi(TAG, "Opening url: $url")
    prepareCustomTab().launchUrl(this, url)
}

suspend fun resolveRedirect(url: String): Uri? {
    if (url[0] != '/')
        return url.asUri()

    return try {
        val response: Deferred<Response>
        response = async(CommonPool) {
            val request = Request.Builder().url(combinePath(API_URL, url)).build()
            HTTP_CLIENT.newCall(request).execute()
        }
        response.await().request().url().toString().asUri()
    } catch (e: IOException) {
        logw(TAG, "Error resolving internal redirect", e)
        null
    }
}
