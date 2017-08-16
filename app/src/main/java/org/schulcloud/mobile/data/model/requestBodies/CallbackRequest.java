package org.schulcloud.mobile.data.model.requestBodies;

public class CallbackRequest {
    public static final String TYPE_RECEIVED = "received";

    public String notificationId;
    public String type;

    public CallbackRequest(String notificationId, String type) {
        this.notificationId = notificationId;
        this.type = type;
    }
}
