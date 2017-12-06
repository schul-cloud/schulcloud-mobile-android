package org.schulcloud.mobile.data.datamanagers;

import org.schulcloud.mobile.data.local.FileStorageDatabasehelper;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.model.Directory;
import org.schulcloud.mobile.data.model.File;
import org.schulcloud.mobile.data.model.requestBodies.SignedUrlRequest;
import org.schulcloud.mobile.data.model.responseBodies.FilesResponse;
import org.schulcloud.mobile.data.model.responseBodies.SignedUrlResponse;
import org.schulcloud.mobile.data.remote.RestService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.functions.Func1;

@Singleton
public class FileDataManager {
    private final RestService mRestService;
    private final FileStorageDatabasehelper mDatabaseHelper;

    @Inject
    PreferencesHelper mPreferencesHelper;
    @Inject
    UserDataManager userDataManager;

    @Inject
    public FileDataManager(RestService restService, PreferencesHelper preferencesHelper,
                           FileStorageDatabasehelper databaseHelper) {
        mRestService = restService;
        mPreferencesHelper = preferencesHelper;
        mDatabaseHelper = databaseHelper;
    }

    public PreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

    public Observable<File> syncFiles(String path) {
        return mRestService.getFiles(userDataManager.getAccessToken(), path)
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
        return mRestService.getFiles(userDataManager.getAccessToken(), path)
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
        return mRestService.deleteDirectory(userDataManager.getAccessToken(), path);
    }

    public Observable<SignedUrlResponse> getFileUrl(SignedUrlRequest signedUrlRequest) {
        return mRestService.generateSignedUrl(userDataManager.getAccessToken(), signedUrlRequest);
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
        return mRestService.deleteFile(userDataManager.getAccessToken(), path);
    }

    public String getCurrentStorageContext() {
        String storageContext = mPreferencesHelper.getCurrentStorageContext();
        // personal files are default
        return storageContext.equals("null") ? "users/" + userDataManager.getCurrentUserId() + "/" : storageContext + "/";
    }

    public void setCurrentStorageContext(String newStorageContext) {
        mPreferencesHelper.saveCurrentStorageContext(newStorageContext);
    }
}
