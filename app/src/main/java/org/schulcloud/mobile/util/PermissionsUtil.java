package org.schulcloud.mobile.util;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class PermissionsUtil {
    @Deprecated
    public static boolean checkPermissions(int callbackId, @NonNull Activity activity,
            @NonNull String... permissionIds) {
        boolean permissions = true;
        for (String p : permissionIds)
            permissions = permissions
                    && ContextCompat
                    .checkSelfPermission(activity, p) == PERMISSION_GRANTED;

        if (!permissions) {
            ActivityCompat.requestPermissions(activity, permissionIds, callbackId);
            return false;
        }

        return true;
    }
}
