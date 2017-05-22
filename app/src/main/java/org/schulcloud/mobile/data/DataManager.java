package org.schulcloud.mobile.data;

import android.util.Log;

import org.schulcloud.mobile.data.local.DatabaseHelper;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.model.AccessToken;
import org.schulcloud.mobile.data.model.CurrentUser;
import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.data.model.Directory;
import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.data.model.File;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.data.model.requestBodies.CallbackRequest;
import org.schulcloud.mobile.data.model.requestBodies.Credentials;
import org.schulcloud.mobile.data.model.requestBodies.DeviceRequest;
import org.schulcloud.mobile.data.model.requestBodies.SignedUrlRequest;
import org.schulcloud.mobile.data.model.responseBodies.DeviceResponse;
import org.schulcloud.mobile.data.model.responseBodies.FilesResponse;
import org.schulcloud.mobile.data.model.responseBodies.SignedUrlResponse;
import org.schulcloud.mobile.data.remote.RestService;
import org.schulcloud.mobile.util.JWTUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;
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
                });
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

    public Observable<CurrentUser> syncCurrentUser(String userId) {
        return mRestService.getUser(getAccessToken(), userId).concatMap(new Func1<CurrentUser, Observable<CurrentUser>>() {
            @Override
            public Observable<CurrentUser> call(CurrentUser currentUser) {
                mPreferencesHelper.saveCurrentUsername(currentUser.displayName);
                return mDatabaseHelper.setCurrentUser(currentUser);
            }
        });
    }

    public Observable<CurrentUser> getCurrentUser() {
        return mDatabaseHelper.getCurrentUser().distinct();
    }

    public String getCurrentUserId() {
        return mPreferencesHelper.getCurrentUserId();
    }


    /**** FileStorage ****/

    public Observable<File> syncFiles(String path) {
        // "users/" + getCurrentUserId()
        return mRestService.getFiles(getAccessToken(), path)
                .concatMap(new Func1<FilesResponse, Observable<File>>() {
                    @Override
                    public Observable<File> call(FilesResponse filesResponse) {
                        // clear old files
                        mDatabaseHelper.clearTable(File.class);
                        return mDatabaseHelper.setFiles(filesResponse.files);
                    }
                });
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
                if (f.path.equals(currentContext)) filteredFiles.add(f);
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
                });

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

    public Observable<SignedUrlResponse> getFileUrl(SignedUrlRequest signedUrlRequest) {
        return mRestService.generateSignedUrl(this.getAccessToken(), signedUrlRequest);
    }

    public Observable<ResponseBody> downloadFile(String url) {
        return mRestService.downloadFile(url);
    }

    public String getCurrentStorageContext() {
        String storageContext = mPreferencesHelper.getCurrentStorageContext();
        return storageContext.equals("null") ? "/" : "/" + storageContext + "/";
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
                });
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
        return mRestService.getEvents(
                getAccessToken())
                .concatMap(new Func1<List<Event>, Observable<Event>>() {
                    @Override
                    public Observable<Event> call(List<Event> events) {
                        // clear old events
                        mDatabaseHelper.clearTable(Event.class);
                        return mDatabaseHelper.setEvents(events);
                    }
                }).doOnError(throwable -> System.err.println(throwable.getStackTrace()));
    }

    public Observable<List<Event>> getEvents() {
        return mDatabaseHelper.getEvents().distinct();
    }


}
