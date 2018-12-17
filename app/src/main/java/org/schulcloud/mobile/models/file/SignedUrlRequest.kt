package org.schulcloud.mobile.models.file


class SignedUrlRequest {
    companion object {
        const val ACTION_GET = "getObject"
        const val ACTION_PUT = "putObject"
    }

    var action: String? = null
    var path: String? = null
    var fileType: String? = null
}
