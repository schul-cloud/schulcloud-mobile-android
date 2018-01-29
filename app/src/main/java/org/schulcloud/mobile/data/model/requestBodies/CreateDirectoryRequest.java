package org.schulcloud.mobile.data.model.requestBodies;

import android.support.annotation.NonNull;

public class CreateDirectoryRequest {
    public String path;

    public CreateDirectoryRequest(@NonNull String path) {
        this.path = path;
    }
}
