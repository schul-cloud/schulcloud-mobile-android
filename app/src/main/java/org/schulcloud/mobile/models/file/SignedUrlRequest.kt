package org.schulcloud.mobile.models.file


/**
 * Date: 7/5/2018
 */
class SignedUrlRequest {
    companion object {
        val ACTION_GET = "getObject"
        val ACTION_PUT = "putObject"
    }

    var action: String? = null
    var path: String? = null
    var fileType: String? = null
}
