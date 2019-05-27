@file:Suppress("TooManyFunctions")

package org.schulcloud.mobile.utils

import android.util.Base64
import com.google.gson.JsonParser
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

object JwtUtil {
    private const val KEY_USER_ID = "userId"

    fun decodeToCurrentUser(jwtEncoded: String): String? {
        @Throws(UnsupportedEncodingException::class)
        fun String.decodeBase64(): String {
            val decodedBytes = Base64.decode(this, Base64.URL_SAFE)
            return String(decodedBytes, Charset.forName("UTF-8"))
        }

        return try {
            val bodyRaw = jwtEncoded.split("\\.".toRegex())
                    .dropLastWhile { it.isEmpty() }[1]
                    .decodeBase64()
            JsonParser().parse(bodyRaw).asJsonObject
                    .get(KEY_USER_ID).asString
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            null
        }
    }
}
