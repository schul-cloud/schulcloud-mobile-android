package org.schulcloud.mobile.util;

import android.support.annotation.NonNull;

import java.io.File;

public final class PathUtil {
    @NonNull
    public static String trimLeadingSlash(@NonNull String path) {
        if (path.length() > 0 && path.charAt(0) == File.separatorChar)
            return path.substring(1);
        return path;
    }
    @NonNull
    public static String trimTrailingSlash(@NonNull String path) {
        if (path.length() > 1 && path.charAt(path.length() - 1) == File.separatorChar)
            return path.substring(0, path.length() - 1);
        return path;
    }
    @NonNull
    public static String trimSlashes(@NonNull String path) {
        return trimTrailingSlash(trimLeadingSlash(path));
    }
    @NonNull
    public static String ensureTrailingSlash(@NonNull String path) {
        if (path.length() == 0 || path.charAt(path.length() - 1) != File.separatorChar)
            return path + File.separator;
        return path;
    }

    @NonNull
    public static String[] getAllParts(@NonNull String path) {
        return path.split(File.separator);
    }
    @NonNull
    public static String combine(@NonNull String... parts) {
        StringBuilder builder = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            boolean endsWithSeparator = builder.length() > 0
                    && builder.charAt(builder.length() - 1) == File.separatorChar;
            boolean beginsWithSeparator = parts[i].length() > 0
                    && parts[i].charAt(0) == File.separatorChar;

            if (endsWithSeparator && beginsWithSeparator)
                builder.append(parts[i].substring(1));
            else if (!endsWithSeparator && !beginsWithSeparator)
                builder.append(File.separatorChar).append(parts[i]);
            else
                builder.append(parts[i]);
        }
        return builder.toString();
    }

    @NonNull
    public static String parent(@NonNull String path) {
        return path.substring(0, trimTrailingSlash(path).lastIndexOf(java.io.File.separator));
    }
}
