package org.schulcloud.mobile;

final class PathResolver {

    private static final String MODULE_NAME = "app";

    private PathResolver() {
    }

    static String resolveAndroidManifestPath() {
        final String projectDirectory = System.getProperty("user.dir");

        return projectDirectory.endsWith(MODULE_NAME) ?
                String.format("%s/src/main/AndroidManifest.xml", projectDirectory) :
                String.format("%s/%s/src/main/AndroidManifest.xml", projectDirectory, MODULE_NAME);
    }

    static String resolveResPath() {
        final String projectDirectory = System.getProperty("user.dir");

        return projectDirectory.endsWith(MODULE_NAME) ?
                String.format("%s/../app/src/main/res", projectDirectory) :
                String.format("%s/app/src/main/res", projectDirectory);
    }

    static String resolveAssetsPath() {
        final String projectDirectory = System.getProperty("user.dir");

        return projectDirectory.endsWith(MODULE_NAME) ?
                String.format("%s/../app/src/main/assets", projectDirectory) :
                String.format("%s/app/src/main/assets", projectDirectory);
    }

}