package org.schulcloud.mobile.data.model.responseBodies;

import com.google.gson.annotations.SerializedName;

public class SignedUrlResponse {
    public String url;
    public SignedUrlResponseHeader header;


    public static class SignedUrlResponseHeader {
        @SerializedName("Content-Type")
        private String contentType;

        @SerializedName("x-amz-meta-path")
        private String metaPath;

        @SerializedName("x-amz-meta-name")
        private String metaName;

        @SerializedName("x-amz-meta-flat-name")
        private String metaFlatName;

        @SerializedName("x-amz-meta-thumbnail")
        private String metaThumbnail;

        public SignedUrlResponseHeader() {

        }

        public String getContentType() {
            return contentType;
        }
        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getMetaPath() {
            return metaPath;
        }
        public void setMetaPath(String metaPath) {
            this.metaPath = metaPath;
        }

        public String getMetaName() {
            return metaName;
        }
        public void setMetaName(String metaName) {
            this.metaName = metaName;
        }

        public String getMetaFlatName() {
            return metaFlatName;
        }
        public void setMetaFlatName(String metaFlatName) {
            this.metaFlatName = metaFlatName;
        }

        public String getMetaThumbnail() {
            return metaThumbnail;
        }
        public void setMetaThumbnail(String metaThumbnail) {
            this.metaThumbnail = metaThumbnail;
        }
    }
}
