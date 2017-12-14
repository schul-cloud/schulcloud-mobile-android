package org.schulcloud.mobile.data.model.requestBodies;

public class SignedUrlRequest {
    public final static String ACTION_OBJECT_GET = "getObject";
    public final static String ACTION_OBJECT_PUT = "putObject";

    public String action;
    public String path;
    public String fileType;

    public SignedUrlRequest(String action, String path, String fileType) {
        this.action = action;
        this.path = path;
        this.fileType = fileType;
    }
}
