package org.schulcloud.mobile.models.file

import com.google.gson.annotations.SerializedName


/**
 * Date: 7/5/2018
 */
class SignedUrlResponse {
    var url: String? = null
    var header: SignedUrlResponseHeader? = null


    class SignedUrlResponseHeader {
        @SerializedName("Content-Type")
        var contentType: String? = null

        @SerializedName("x-amz-meta-name")
        var metaName: String? = null

        @SerializedName("x-amz-meta-flat-name")
        var metaFlatName: String? = null

        @SerializedName("x-amz-meta-thumbnail")
        var metaThumbnail: String? = null
    }
}
