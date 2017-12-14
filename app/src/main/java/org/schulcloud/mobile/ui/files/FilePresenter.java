package org.schulcloud.mobile.ui.files;

import android.util.Log;

import com.ipaulpro.afilechooser.utils.FileUtils;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.File;
import org.schulcloud.mobile.data.model.requestBodies.SignedUrlRequest;
import org.schulcloud.mobile.data.model.responseBodies.SignedUrlResponse;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@ConfigPersistent
public class FilePresenter extends BasePresenter<FileMvpView> {
    private Subscription fileSubscription;
    private Subscription directorySubscription;
    private Subscription fileGetterSubscription;
    private Subscription fileDownloadSubscription;
    private Subscription fileUploadSubscription;
    private Subscription fileStartUploadSubscription;
    private Subscription fileDeleteSubscription;
    private Subscription directoryDeleteSubscription;

    @Inject
    public FilePresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(FileMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(fileSubscription);
        RxUtil.unsubscribe(directorySubscription);
        RxUtil.unsubscribe(fileGetterSubscription);
        RxUtil.unsubscribe(fileDownloadSubscription);
        RxUtil.unsubscribe(fileUploadSubscription);
        RxUtil.unsubscribe(fileStartUploadSubscription);
        RxUtil.unsubscribe(fileDeleteSubscription);
        RxUtil.unsubscribe(directoryDeleteSubscription);
    }

    public void loadFiles() {
        checkViewAttached();
        RxUtil.unsubscribe(fileSubscription);
        fileSubscription = mDataManager.getFiles()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        files -> getMvpView().showFiles(files),
                        // onError
                        error -> {
                            Timber.e(error, "There was an error loading the files.");
                            getMvpView().showError();
                        });
    }
    public void loadDirectories() {
        checkViewAttached();
        RxUtil.unsubscribe(directorySubscription);
        directorySubscription = mDataManager.getDirectories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        directories -> getMvpView().showDirectories(directories),
                        // onError
                        error -> {
                            Timber.e(error, "There was an error loading the directories.");
                            getMvpView().showError();
                        });
    }

    /**
     * Loads a file from the Schul-Cloud server.
     *
     * @param file     {File} - the db-saved file
     * @param download {Boolean} - whether to download the file or not
     */
    public void loadFileFromServer(File file, Boolean download) {
        checkViewAttached();
        RxUtil.unsubscribe(fileGetterSubscription);
        fileGetterSubscription = mDataManager.getFileUrl(new SignedUrlRequest(
                SignedUrlRequest.ACTION_OBJECT_GET, // action
                file.key, // path
                file.type // fileType
        )).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (signedUrlResponse) -> {
                            Log.d("Fetched file url", signedUrlResponse.url);

                            if (download)
                                downloadFile(signedUrlResponse.url, file.name);
                            else
                                getMvpView().showFile(
                                        signedUrlResponse.url,
                                        signedUrlResponse.header.getContentType());
                        },
                        error -> {
                            Timber.e(error, "There was an error loading file from Server.");
                            getMvpView().showLoadingFileFromServerError();
                        });
    }

    /**
     * Downloads a file from a given url.
     *
     * @param url      {String} - the remote url from which the file will be downloaded
     * @param fileName {String} - the name of the downloaded file
     */
    public void downloadFile(String url, String fileName) {
        checkViewAttached();
        RxUtil.unsubscribe(fileDownloadSubscription);
        fileDownloadSubscription = mDataManager.downloadFile(url)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseBody -> getMvpView().saveFile(responseBody, fileName),
                        error -> {
                            Timber.e(error, "There was an error loading file from Server.");
                            getMvpView().showLoadingFileFromServerError();
                        });
    }


    /**
     * Opens a directory by fetching files for new storageContext.
     *
     * @param dirName {String} - the directory's name for which the files will be fetched
     */
    public void goIntoDirectory(String dirName) {
        mDataManager.setCurrentStorageContext(dirName);
        getMvpView().reloadFiles();
    }

    /**
     * Uploads a local file to the server.
     *
     * @param fileToUpload {File} - the file which will be uploaded
     */
    public void uploadFileToServer(java.io.File fileToUpload) {
        checkViewAttached();
        RxUtil.unsubscribe(fileUploadSubscription);

        // todo: refactor later on when there are class and course folders
        String uploadPath = mDataManager.getCurrentStorageContext() + fileToUpload.getName();

        SignedUrlRequest signedUrlRequest = new SignedUrlRequest(
                SignedUrlRequest.ACTION_OBJECT_PUT, // action
                uploadPath,
                FileUtils.getMimeType(fileToUpload));

        fileUploadSubscription = mDataManager.getFileUrl(signedUrlRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        signedUrlResponse -> startUploading(fileToUpload, signedUrlResponse),
                        error -> {
                            Timber.e(error, "There was an error uploading file from Server.");
                            getMvpView().showUploadFileError();
                        });
    }

    /**
     * Initiates a download task.
     *
     * @param file     {File} - the file which will be downloaded
     * @param download {Boolean} - whether to download on hard disk
     */
    public void startDownloading(File file, boolean download) {
        getMvpView().startDownloading(file, download);
    }
    /**
     * Starts an upload progress to the given url.
     *
     * @param file              {File} - the file which will be uploaded
     * @param signedUrlResponse {SignedUrlResponse} - contains information about the uploaded file
     */
    public void startUploading(java.io.File file, SignedUrlResponse signedUrlResponse) {
        checkViewAttached();
        RxUtil.unsubscribe(fileStartUploadSubscription);
        fileStartUploadSubscription = mDataManager.uploadFile(file, signedUrlResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseBody -> getMvpView().reloadFiles(),
                        error -> {
                            Timber.e(error, "There was an error uploading file from Server.");
                            getMvpView().showUploadFileError();
                        });

    }

    /**
     * Deletes a file from the server.
     *
     * @param path {String} - the key/path to the file
     */
    public void deleteFile(String path) {
        checkViewAttached();
        RxUtil.unsubscribe(fileDeleteSubscription);
        fileDeleteSubscription = mDataManager.deleteFile(path)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseBody -> getMvpView().showFileDeleteSuccess(),
                        error -> {
                            Timber.e(error, "There was an error deleting file from Server.");
                            getMvpView().showFileDeleteError();
                        });

    }
    /**
     * Deletes a directory from the server.
     *
     * @param path {String} - the key/path to the directory
     */
    public void deleteDirectory(String path) {
        checkViewAttached();
        RxUtil.unsubscribe(directoryDeleteSubscription);
        directoryDeleteSubscription = mDataManager.deleteDirectory(path)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseBody -> getMvpView().showDirectoryDeleteSuccess(),
                        error -> {
                            Timber.e(error, "There was an error deleting file from Server.");
                            getMvpView().showFileDeleteError();
                        });

    }

    public void startDirectoryDeleting(String path, String dirName) {
        getMvpView().startDirectoryDeleting(path, dirName);
    }
    public void startFileDeleting(String path, String fileName) {
        getMvpView().startFileDeleting(path, fileName);
    }

    /**
     * Checks the current storage path and steps back if it's not a root directory.
     *
     * @return True if stepping back was successful, false if we are already in the root directory.
     */
    public boolean stepOneDirectoryBack() {
        String currentPath = mDataManager.getCurrentStorageContext();

        // remove last slash
        if (currentPath.lastIndexOf(java.io.File.separator) == (currentPath.length() - 1))
            currentPath = currentPath.substring(0, currentPath.length() - 1);

        // first two parts are meta
        if (currentPath.split(java.io.File.separator).length > 2) {
            currentPath = currentPath.substring(0, currentPath.lastIndexOf(java.io.File.separator));
            mDataManager.setCurrentStorageContext(currentPath);
            getMvpView().reloadFiles();
            return true;
        }
        return false;
    }
}

