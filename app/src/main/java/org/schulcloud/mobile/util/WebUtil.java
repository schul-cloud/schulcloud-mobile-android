package org.schulcloud.mobile.util;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.schulcloud.mobile.BuildConfig;
import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.datamanagers.FileDataManager;
import org.schulcloud.mobile.ui.courses.CourseFragment;
import org.schulcloud.mobile.ui.courses.detailed.DetailedCourseFragment;
import org.schulcloud.mobile.ui.courses.topic.TopicFragment;
import org.schulcloud.mobile.ui.dashboard.DashboardFragment;
import org.schulcloud.mobile.ui.files.FilesFragment;
import org.schulcloud.mobile.ui.files.overview.FileOverviewFragment;
import org.schulcloud.mobile.ui.homework.HomeworkFragment;
import org.schulcloud.mobile.ui.homework.detailed.DetailedHomeworkFragment;
import org.schulcloud.mobile.ui.main.MainActivity;
import org.schulcloud.mobile.ui.main.MainFragment;
import org.schulcloud.mobile.ui.news.NewsFragment;
import org.schulcloud.mobile.ui.news.detailed.DetailedNewsFragment;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Single;

/**
 * Date: 2/17/2018
 */
public final class WebUtil {
    private static final String TAG = WebUtil.class.getSimpleName();

    public static final String HEADER_COOKIE = "cookie";
    public static final String HEADER_CONTENT_TYPE = "content-type";
    public static final String HEADER_CONTENT_ENCODING = "content-encoding";

    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String MIME_TEXT_HTML = "text/html";
    public static final String MIME_APPLICATION_JSON = "application/json";

    public static final String ENCODING_UTF_8 = "utf-8";

    public static final String SCHEME_HTTP = "http";
    public static final String SCHEME_HTTPS = "https";

    public static final String HOST_SCHULCLOUD_ORG = "schulcloud.org";
    public static final String HOST_SCHUL_CLOUD_ORG = "schul-cloud.org";

    public static final String URL_BASE_API = BuildConfig.URL_ENDPOINT;
    public static final String URL_BASE = SCHEME_HTTPS + "://" + HOST_SCHUL_CLOUD_ORG;

    // Internal paths
    public static final String PATH_INTERNAL_DASHBOARD = "dashboard";

    public static final String PATH_INTERNAL_NEWS = "news";

    public static final String PATH_INTERNAL_COURSES = "courses";
    public static final String PATH_INTERNAL_COURSES_TOPICS = "topics";
    public static final String PATH_INTERNAL_COURSES_TOOLS = "tools";
    public static final String PATH_INTERNAL_COURSES_GROUPS = "groups";

    public static final String PATH_INTERNAL_HOMEWORK = "homework";
    public static final String PATH_INTERNAL_HOMEWORK_ASKED = "asked";
    public static final String PATH_INTERNAL_HOMEWORK_PRIVATE = "private";
    public static final String PATH_INTERNAL_HOMEWORK_ARCHIVE = "archive";
    public static final String[] PATHS_INTERNAL_HOMEWORK = {
            PATH_INTERNAL_HOMEWORK_ASKED,
            PATH_INTERNAL_HOMEWORK_PRIVATE,
            PATH_INTERNAL_HOMEWORK_ARCHIVE};

    public static final String PATH_INTERNAL_FILES = "files";
    public static final String PATH_INTERNAL_FILES_PARAM_DIR = "dir";
    public static final String PATH_INTERNAL_FILES_PARAM_PATH = "path";
    public static final String PATH_INTERNAL_FILES_PARAM_FILE = "file";
    public static final String PATH_INTERNAL_FILES_MY = "my";
    public static final String PATH_INTERNAL_FILES_COURSES = "courses";
    public static final String PATH_INTERNAL_FILES_SHARED = "shared";
    public static final String PATH_INTERNAL_FILES_FILE = "file";
    public static final String[] PATHS_INTERNAL_FILES = {
            PATH_INTERNAL_FILES_MY,
            PATH_INTERNAL_FILES_COURSES,
            PATH_INTERNAL_FILES_SHARED,
            PATH_INTERNAL_FILES_FILE};

    public static final String[] PATHS_INTERNAL = {
            PATH_INTERNAL_DASHBOARD,
            PATH_INTERNAL_NEWS,
            PATH_INTERNAL_COURSES,
            PATH_INTERNAL_HOMEWORK,
            PATH_INTERNAL_FILES};

    @NonNull
    public static Single<Uri> resolveRedirect(@NonNull String url, @NonNull String accessToken) {
        if (url.charAt(0) != '/')
            return Single.just(Uri.parse(url));

        return Single.create(subscriber -> {
            OkHttpClient okHttpClient =
                    new OkHttpClient().newBuilder().addInterceptor(chain -> chain
                            .proceed(chain.request().newBuilder()
                                    .addHeader(HEADER_COOKIE, "jwt=" + accessToken).build()))
                            .build();
            try {
                Response response = okHttpClient
                        .newCall(new Request.Builder().url(PathUtil.combine(URL_BASE_API, url))
                                .build())
                        .execute();
                subscriber.onSuccess(Uri.parse(response.request().url().toString()));
            } catch (IOException e) {
                Log.w(TAG, "Error resolving internal redirect", e);
                subscriber.onError(e);
            }
        });
    }

    @NonNull
    public static CustomTabsIntent newCustomTab(@NonNull Context context) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(context, R.color.hpiRed));
        builder.setStartAnimations(context, R.anim.slide_in_right, R.anim.slide_out_left);
        builder.setExitAnimations(context, R.anim.slide_in_left, R.anim.slide_out_right);
        builder.addDefaultShareMenuItem();
        return builder.build();
    }

    public static void openUrl(@NonNull MainActivity mainActivity, @NonNull Uri url) {
        openUrl(mainActivity, null, url);
    }
    public static void openUrl(@NonNull MainActivity mainActivity, @Nullable MainFragment fragment,
            @NonNull Uri url) {
        Log.i(TAG, "Opening url: " + url);
        String scheme = url.getScheme().toLowerCase();
        String host = url.getHost().toLowerCase();
        List<String> path = url.getPathSegments();
        String pathPrefix = null;
        String pathEnd = null;
        if (!path.isEmpty()) {
            pathPrefix = path.get(0);
            pathEnd = path.get(path.size() - 1).toLowerCase();
        }

        // Internal links can be handled by the app
        if ((scheme.equals(SCHEME_HTTP) || scheme.equals(SCHEME_HTTPS))
                && (host.equals(HOST_SCHULCLOUD_ORG) || host.equals(HOST_SCHUL_CLOUD_ORG))
                && path.size() > 0
                && ListUtils.contains(PATHS_INTERNAL, pathPrefix)) {
            MainFragment newFragment = null;
            assert pathPrefix != null;
            switch (pathPrefix) {
                case PATH_INTERNAL_DASHBOARD:
                    if (!(fragment instanceof DashboardFragment))
                        newFragment = DashboardFragment.newInstance();
                    break;

                case PATH_INTERNAL_NEWS:
                    if (path.size() == 1)
                        newFragment = NewsFragment.newInstance();
                    else if (path.size() == 2)
                        newFragment = DetailedNewsFragment.newInstance(pathEnd);
                    break;

                case PATH_INTERNAL_COURSES:
                    if (path.size() == 1)
                        newFragment = CourseFragment.newInstance();
                    else if (path.size() == 2 || path.size() == 3)
                        newFragment = DetailedCourseFragment.newInstance(pathEnd);
                    else if (path.size() == 4 && path.get(3).equals(PATH_INTERNAL_COURSES_TOPICS))
                        newFragment = TopicFragment.newInstance(pathEnd);
                    break;

                case PATH_INTERNAL_HOMEWORK:
                    if (path.size() == 1)
                        newFragment = HomeworkFragment.newInstance();
                    else if (path.size() == 2
                            && !ListUtils.contains(PATHS_INTERNAL_HOMEWORK, pathEnd))
                        newFragment = DetailedHomeworkFragment.newInstance(pathEnd);
                    break;

                case PATH_INTERNAL_FILES:
                    if (path.size() == 1)
                        newFragment = FileOverviewFragment.newInstance(false);
                    else if (path.size() == 2
                            && ListUtils.contains(PATHS_INTERNAL_FILES, pathEnd)) {
                        String[] filePath = parseUrlPath(url);
                        if (filePath != null)
                            newFragment = FilesFragment.newInstance(filePath[0], filePath[1]);
                    }
                    break;
            }
            Log.i(TAG, "Chosen fragment: " + newFragment);
            if (newFragment != null) {
                if (fragment == null)
                    mainActivity.addFragment(newFragment);
                else
                    mainActivity.addFragment(fragment, newFragment);
                return;
            } else {
                Toast.makeText(mainActivity, R.string.web_error_linkNotSupported,
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

        newCustomTab(mainActivity).launchUrl(mainActivity, url);
    }
    @Nullable
    private static String[] parseUrlPath(@NonNull Uri url) {
        List<String> segments = url.getPathSegments();
        if (!segments.get(0).toLowerCase().equals(PATH_INTERNAL_FILES))
            return null;

        String path;
        String file = null;
        String dir;
        switch (segments.get(1).toLowerCase()) {
            case PATH_INTERNAL_FILES_MY:
                path = FileDataManager.CONTEXT_MY;
                dir = url.getQueryParameter(PATH_INTERNAL_FILES_PARAM_DIR);
                if (dir != null)
                    path = PathUtil.combine(path, dir);
                break;

            case PATH_INTERNAL_FILES_COURSES:
                path = FileDataManager.CONTEXT_COURSES;
                if (segments.size() > 2) {
                    path = PathUtil.combine(path, segments.get(2));
                    dir = url.getQueryParameter(PATH_INTERNAL_FILES_PARAM_DIR);
                    if (dir != null)
                        path = PathUtil.combine(path, dir);
                }
                break;

            case PATH_INTERNAL_FILES_FILE:
                path = url.getQueryParameter(PATH_INTERNAL_FILES_PARAM_FILE);
                if (TextUtils.isEmpty(path))
                    path = url.getQueryParameter(PATH_INTERNAL_FILES_PARAM_PATH);

                String[] pathSegments = PathUtil.getAllParts(path);
                file = pathSegments[pathSegments.length - 1];
                path = PathUtil.combine(Arrays.copyOf(pathSegments, pathSegments.length - 1));
                break;

            case PATH_INTERNAL_FILES_SHARED: // Not supported yet
            default:
                return null;
        }
        return new String[]{path, file};
    }
}
