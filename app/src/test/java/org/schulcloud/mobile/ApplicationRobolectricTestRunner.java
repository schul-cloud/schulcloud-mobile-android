package org.schulcloud.mobile;


import android.os.Build;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.Fs;

public class ApplicationRobolectricTestRunner extends RobolectricGradleTestRunner {

    private static final int TARGET_SDK_VERSION = Build.VERSION_CODES.N;
    private static final int MIN_SDK_VERSION = Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;

    public ApplicationRobolectricTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected AndroidManifest getAppManifest(Config config) {

        final String manifestPath = PathResolver.resolveAndroidManifestPath();
        final String resourcesPath = PathResolver.resolveResPath();
        final String assetsPath = PathResolver.resolveAssetsPath();

        AndroidManifest manifest = new AndroidManifest(
                Fs.fileFromPath(manifestPath),
                Fs.fileFromPath(resourcesPath),
                Fs.fileFromPath(assetsPath)) {
            @Override
            public int getTargetSdkVersion() {
                return TARGET_SDK_VERSION;
            }

            @Override
            public int getMinSdkVersion() {
                return MIN_SDK_VERSION;
            }
        };

        return manifest;
    }


}