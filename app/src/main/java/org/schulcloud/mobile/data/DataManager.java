package org.schulcloud.mobile.data;

import android.util.Log;

import org.schulcloud.mobile.data.local.DatabaseHelper;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.model.AccessToken;
import org.schulcloud.mobile.data.model.Contents;
import org.schulcloud.mobile.data.model.Course;
import org.schulcloud.mobile.data.model.CurrentUser;
import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.data.model.Directory;
import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.data.model.File;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.News;
import org.schulcloud.mobile.data.model.Submission;
import org.schulcloud.mobile.data.model.Topic;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.data.model.requestBodies.AddHomeworkRequest;
import org.schulcloud.mobile.data.model.requestBodies.CallbackRequest;
import org.schulcloud.mobile.data.model.requestBodies.Credentials;
import org.schulcloud.mobile.data.model.requestBodies.DeviceRequest;
import org.schulcloud.mobile.data.model.requestBodies.FeedbackRequest;
import org.schulcloud.mobile.data.model.requestBodies.SignedUrlRequest;
import org.schulcloud.mobile.data.model.responseBodies.AddHomeworkResponse;
import org.schulcloud.mobile.data.model.responseBodies.DeviceResponse;
import org.schulcloud.mobile.data.model.responseBodies.FeathersResponse;
import org.schulcloud.mobile.data.model.responseBodies.FeedbackResponse;
import org.schulcloud.mobile.data.model.responseBodies.FilesResponse;
import org.schulcloud.mobile.data.model.responseBodies.SignedUrlResponse;
import org.schulcloud.mobile.data.remote.RestService;
import org.schulcloud.mobile.util.crypt.JWTUtil;
import org.schulcloud.mobile.util.Pair;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Func1;

@Singleton
public class DataManager {

    private final RestService mRestService;
    private final DatabaseHelper mDatabaseHelper;
    private final PreferencesHelper mPreferencesHelper;

    @Inject
    public DataManager(RestService restService, PreferencesHelper preferencesHelper,
                       DatabaseHelper databaseHelper) {
        mRestService = restService;
        mPreferencesHelper = preferencesHelper;
        mDatabaseHelper = databaseHelper;
    }

    public PreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

    /**** User ****/

    public Observable<User> syncUsers() {
        return mRestService.getUsers(getAccessToken())
                .concatMap(new Func1<List<User>, Observable<User>>() {
                    @Override
                    public Observable<User> call(List<User> users) {
                        return mDatabaseHelper.setUsers(users);
                    }
                }).doOnError(Throwable::printStackTrace);
    }

    public Observable<List<User>> getUsers() {
        return mDatabaseHelper.getUsers().distinct();
    }

    public String getAccessToken() {
        return mPreferencesHelper.getAccessToken();
    }

    public Observable<CurrentUser> signIn(String username, String password) {
        return mRestService.signIn(new Credentials(username, password))
                .concatMap(new Func1<AccessToken, Observable<CurrentUser>>() {
                    @Override
                    public Observable<CurrentUser> call(AccessToken accessToken) {
                        // save current user data
                        String jwt = mPreferencesHelper.saveAccessToken(accessToken);
                        String currentUser = JWTUtil.decodeToCurrentUser(jwt);
                        mPreferencesHelper.saveCurrentUserId(currentUser);

                        return syncCurrentUser(currentUser);
                    }
                });
    }
    public void signOut() {
        mDatabaseHelper.clearAll();
        mPreferencesHelper.clear();
    }

    public Observable<CurrentUser> syncCurrentUser(String userId) {
        return mRestService.getUser(getAccessToken(), userId).concatMap(new Func1<CurrentUser, Observable<CurrentUser>>() {
            @Override
            public Observable<CurrentUser> call(CurrentUser currentUser) {
                mPreferencesHelper.saveCurrentUsername(currentUser.displayName);
                mPreferencesHelper.saveCurrentSchoolId(currentUser.schoolId);
                return mDatabaseHelper.setCurrentUser(currentUser);
            }
        }).doOnError(Throwable::printStackTrace);
    }

    public Observable<CurrentUser> getCurrentUser() {
        return mDatabaseHelper.getCurrentUser().distinct();
    }

    public String getCurrentUserId() {
        return mPreferencesHelper.getCurrentUserId();
    }


    /**** FileStorage ****/

    public Observable<File> syncFiles(String path) {
        return mRestService.getFiles(getAccessToken(), path)
                .concatMap(new Func1<FilesResponse, Observable<File>>() {
                    @Override
                    public Observable<File> call(FilesResponse filesResponse) {
                        // clear old files
                        mDatabaseHelper.clearTable(File.class);

                        List<File> files = new ArrayList<>();

                        // set fullPath for every file
                        for (File file : filesResponse.files) {
                            file.fullPath = file.key.substring(0, file.key.lastIndexOf(java.io.File.separator));
                            files.add(file);
                        }

                        return mDatabaseHelper.setFiles(files);
                    }
                }).doOnError(Throwable::printStackTrace);
    }

    public Observable<List<File>> getFiles() {
        return mDatabaseHelper.getFiles().distinct().concatMap(files -> {
            List<File> filteredFiles = new ArrayList<File>();
            String currentContext = getCurrentStorageContext();
            // remove last trailing slash
            if (!currentContext.equals("/") && currentContext.endsWith("/")) {
                currentContext = currentContext.substring(0, currentContext.length() - 1);
            }

            for (File f : files) {
                if (f.fullPath.equals(currentContext)) filteredFiles.add(f);
            }
            return Observable.just(filteredFiles);
        });
    }

    public Observable<Directory> syncDirectories(String path) {
        return mRestService.getFiles(getAccessToken(), path)
                .concatMap(new Func1<FilesResponse, Observable<Directory>>() {
                    @Override
                    public Observable<Directory> call(FilesResponse filesResponse) {
                        // clear old directories
                        mDatabaseHelper.clearTable(Directory.class);

                        List<Directory> improvedDirs = new ArrayList<Directory>();
                        for(Directory d : filesResponse.directories) {
                            d.path = getCurrentStorageContext();
                            improvedDirs.add(d);
                        }

                        return mDatabaseHelper.setDirectories(improvedDirs);
                    }
                }).doOnError(Throwable::printStackTrace);

    }

    public Observable<List<Directory>> getDirectories() {
        return mDatabaseHelper.getDirectories().distinct().concatMap(directories -> {
            List<Directory> filteredDirectories = new ArrayList<Directory>();
            for (Directory d : directories) {
                if (d.path.equals(getCurrentStorageContext())) filteredDirectories.add(d);
            }
            return Observable.just(filteredDirectories);
        });
    }

    public Observable<ResponseBody> deleteDirectory(String path) {
        return mRestService.deleteDirectory(this.getAccessToken(), path);
    }

    public Observable<SignedUrlResponse> getFileUrl(SignedUrlRequest signedUrlRequest) {
        return mRestService.generateSignedUrl(this.getAccessToken(), signedUrlRequest);
    }

    public Observable<ResponseBody> downloadFile(String url) {
        return mRestService.downloadFile(url);
    }

    public Observable<ResponseBody> uploadFile(java.io.File file, SignedUrlResponse signedUrlResponse) {
        RequestBody requestBody  = RequestBody.create(MediaType.parse("file/*"), file);
        return mRestService.uploadFile(
                signedUrlResponse.url,
                signedUrlResponse.header.getContentType(),
                signedUrlResponse.header.getMetaPath(),
                signedUrlResponse.header.getMetaName(),
                signedUrlResponse.header.getMetaThumbnail(),
                requestBody
        );
    }

    public Observable<ResponseBody> deleteFile(String path) {
        return mRestService.deleteFile(this.getAccessToken(), path);
    }

    public String getCurrentStorageContext() {
        String storageContext = mPreferencesHelper.getCurrentStorageContext();
        // personal files are default
        return storageContext.equals("null") ? "users/" + this.getCurrentUserId() + "/" : storageContext + "/";
    }

    public void setCurrentStorageContext(String newStorageContext) {
        mPreferencesHelper.saveCurrentStorageContext(newStorageContext);
    }

    /**** NotificationService ****/

    public Observable<DeviceResponse> createDevice(DeviceRequest deviceRequest, String token) {
        return mRestService.createDevice(
                getAccessToken(),
                deviceRequest)
                .concatMap(new Func1<DeviceResponse, Observable<DeviceResponse>>() {
                    @Override
                    public Observable<DeviceResponse> call(DeviceResponse deviceResponse) {
                        Log.i("[DEVICE]", deviceResponse.id);
                        mPreferencesHelper.saveMessagingToken(token);
                        return Observable.just(deviceResponse);
                    }
                });
    }

    public Observable<Device> syncDevices() {
        return mRestService.getDevices(getAccessToken())
                .concatMap(new Func1<List<Device>, Observable<Device>>() {
                    @Override
                    public Observable<Device> call(List<Device> devices) {
                        // clear old devices
                        mDatabaseHelper.clearTable(Device.class);
                        return mDatabaseHelper.setDevices(devices);
                    }
                }).doOnError(Throwable::printStackTrace);
    }

    public Observable<List<Device>> getDevices() {
        return mDatabaseHelper.getDevices().distinct();
    }

    public Observable<Response<Void>> sendCallback(CallbackRequest callbackRequest) {
        return mRestService.sendCallback(getAccessToken(), callbackRequest);
    }

    public Observable<Response<Void>> deleteDevice(String deviceId) {
        return mRestService.deleteDevice(getAccessToken(), deviceId);
    }

    /**** Events ****/

    public Observable<Event> syncEvents() {
        return mRestService.getEvents(getAccessToken())
                .concatMap(new Func1<List<Event>, Observable<Event>>() {
                    @Override
                    public Observable<Event> call(List<Event> events) {
                        // clear old events
                        mDatabaseHelper.clearTable(Event.class);
                        return mDatabaseHelper.setEvents(events);
                    }
                }).doOnError(Throwable::printStackTrace);
    }

    public Observable<List<Event>> getEvents() {
        return mDatabaseHelper.getEvents().distinct();
    }

    public Observable<List<Event>> getEventsForToday() {
        return mDatabaseHelper.getEventsForToday();
    }

    /**** Homework ****/

    public Observable<Homework> syncHomework() {
        return mRestService.getHomework(getAccessToken())
                .concatMap(new Func1<List<Homework>, Observable<Homework>>() {
                    @Override
                    public Observable<Homework> call(List<Homework> homeworks) {
                        // clear old devices
                        mDatabaseHelper.clearTable(Homework.class);
                        return mDatabaseHelper.setHomework(homeworks);
                    }
                }).doOnError(Throwable::printStackTrace);
    }

    public Observable<List<Homework>> getHomework() {
        return mDatabaseHelper.getHomework().distinct();
    }

    public Homework getHomeworkForId(String homeworkId) {
        return mDatabaseHelper.getHomeworkForId(homeworkId);
    }

    public Pair<String, String> getOpenHomeworks() {
        return mDatabaseHelper.getOpenHomeworks();
    }

    public Observable<AddHomeworkResponse> addHomework(AddHomeworkRequest addHomeworkRequest) {
        return mRestService.addHomework(getAccessToken(), addHomeworkRequest);
    }

    /**** Submissions ****/

    public Observable<Submission> syncSubmissions() {
        return mRestService.getSubmissions(getAccessToken())
                .concatMap(new Func1<List<Submission>, Observable<Submission>>() {
                    @Override
                    public Observable<Submission> call(List<Submission> submissions) {
                        // clear old devices
                        mDatabaseHelper.clearTable(Submission.class);
                        return mDatabaseHelper.setSubmissions(submissions);
                    }
                }).doOnError(Throwable::printStackTrace);
    }

    public Observable<List<Submission>> getSubmissions() {
        return mDatabaseHelper.getSubmissions().distinct();
    }

    public Submission getSubmissionForId(String homeworkId) {
        return mDatabaseHelper.getSubmissionForId(homeworkId);
    }

    /**** Courses ****/

    public Observable<Course> syncCourses() {
        return mRestService.getCourses(getAccessToken())
                .concatMap(new Func1<FeathersResponse<Course>, Observable<Course>>() {
                    @Override
                    public Observable<Course> call(FeathersResponse<Course> courses) {
                        mDatabaseHelper.clearTable(Course.class);
                        return mDatabaseHelper.setCourses(courses.data);
                    }
                })
                .doOnError(Throwable::printStackTrace);
    }

    public Observable<List<Course>> getCourses() {
        return mDatabaseHelper.getCourses().distinct();
    }

    public Course getCourseForId(String courseId) {
        return mDatabaseHelper.getCourseForId(courseId);
    }

    /**** Topics ****/

    public Observable<Topic> syncTopics(String courseId) {
        return mRestService.getTopics(getAccessToken(), courseId)
                .concatMap(new Func1<FeathersResponse<Topic>, Observable<Topic>>() {
                    @Override
                    public Observable<Topic> call(FeathersResponse<Topic> topics) {
                        mDatabaseHelper.clearTable(Topic.class);
                        return mDatabaseHelper.setTopics(topics.data);
                    }
                })
                .doOnError(Throwable::printStackTrace);
    }

    public Observable<List<Topic>> getTopics() {
        return mDatabaseHelper.getTopics().distinct();
    }

    public List<Contents> getContents(String topicId) {
        return mDatabaseHelper.getContents(topicId).contents;
    }

    /**** Feedback ****/

    public Observable<FeedbackResponse> sendFeedback(FeedbackRequest feedbackRequest) {
        return mRestService.sendFeedback(
                getAccessToken(),
                feedbackRequest)
                .concatMap(new Func1<FeedbackResponse, Observable<FeedbackResponse>>() {
                    @Override
                    public Observable<FeedbackResponse> call(FeedbackResponse feedbackResponse) {
                        return Observable.just(feedbackResponse);
                    }
                });
    }

    /**** News ****/

    public Observable<List<News>> getNews() {
        return mDatabaseHelper.getNews();
    }
    public News getNewsForId(String newsId) {
        return mDatabaseHelper.getNewsForId(newsId);
    }
    public Observable<News> syncNews() {
        return mRestService.getNews(getAccessToken())
                .concatMap(newsFeathersResponse -> {
                    mDatabaseHelper.clearTable(News.class);
                    return mDatabaseHelper.setNews(newsFeathersResponse.data);
                }).doOnError(Throwable::printStackTrace);
    }
}
