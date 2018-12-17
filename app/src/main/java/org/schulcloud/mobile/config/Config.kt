package org.schulcloud.mobile.config

import org.schulcloud.mobile.BuildConfig

object Config {
    val HEADER_AUTH = "Authorization"
    val HEADER_AUTH_VALUE_PREFIX = "Bearer "

    val REALM_SCHEMA_VERSION = 1L

    const val FILE_PROVIDER = "${BuildConfig.APPLICATION_ID}.fileprovider"
}
