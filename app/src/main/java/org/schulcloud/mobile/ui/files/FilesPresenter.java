package org.schulcloud.mobile.ui.files;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ipaulpro.afilechooser.utils.FileUtils;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.CurrentUser;
import org.schulcloud.mobile.data.model.Directory;
import org.schulcloud.mobile.data.model.File;
import org.schulcloud.mobile.data.model.requestBodies.CreateDirectoryRequest;
import org.schulcloud.mobile.data.model.requestBodies.SignedUrlRequest;
import org.schulcloud.mobile.data.model.responseBodies.SignedUrlResponse;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.PathUtil;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@SuppressWarnings("WeakerAccess")
@ConfigPersistent
public class FilesPresenter extends BasePresenter<FilesMvpView> {

    private DataManager mDataManager;
    private Subscription mFileDownloadGetterSubscription;
    private Subscription mFileDownloadSubscription;
    private Subscription mFileUploadGetterSubscription;
    private Subscription mFileUploadSubscription;
    private Subscription mFileDeleteSubscription;

    private Subscription mDirectoryCreateSubscription;
    private Subscription mDirectoryDeleteSubscription;

    @Inject
    FilesPresenter(DataManager dataManager) {
        mDataManager = dataManager;

        mDataManager.getCurrentUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(currentUser -> sendToView(v -> {
                    v.showCanUploadFile(currentUser.hasPermission(
                            CurrentUser.PERMISSION_FILE_CREATE));
                    v.showCanDeleteFiles(currentUser.hasPermission(
                            CurrentUser.PERMISSION_FILE_DELETE));
                    v.showCanCreateDirectories(currentUser.hasPermission(
                            CurrentUser.PERMISSION_FOLDER_CREATE));
                    v.showCanDeleteDirectories(currentUser.hasPermission(
                            CurrentUser.PERMISSION_FOLDER_DELETE));
                }));
        loadBreadcrumbs();
        loadFiles();
        loadDirectories();
    }

    @Override
    protected void onViewAttached(@NonNull FilesMvpView view) {
        super.onViewAttached(view);
        // TODO: Remove when fragment lifecycle is fixed
        loadBreadcrumbs();
    }
    @Override
    protected void onViewDetached() {
        super.onViewDetached();
        RxUtil.unsubscribe(mFileDownloadGetterSubscription);
        RxUtil.unsubscribe(mFileDownloadSubscription);
        RxUtil.unsubscribe(mFileUploadGetterSubscription);
        RxUtil.unsubscribe(mFileUploadSubscription);
        RxUtil.unsubscribe(mFileDeleteSubscription);

        RxUtil.unsubscribe(mDirectoryCreateSubscription);
        RxUtil.unsubscribe(mDirectoryDeleteSubscription);
    }

    private void loadBreadcrumbs() {
        String path = mDataManager.getCurrentStorageContext();
        if (path.startsWith(DataManager.FILES_CONTEXT_MY))
            sendToView(view -> view.showBreadcrumbs(path, null));
        else if (path.startsWith(DataManager.FILES_CONTEXT_COURSES))
            sendToView(view -> view.showBreadcrumbs(path,
                    mDataManager.getCourseForId(path.split("/", 3)[1])));
    }

    private void loadFiles() {
        mDataManager.getFiles()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        files -> sendToView(view -> view.showFiles(files)),
                        error -> {
                            Timber.e(error, "There was an error loading the files.");
                            sendToView(FilesMvpView::showFilesLoadError);
                        });
    }

    /* File download */
    public void onFileSelected(@NonNull File file) {
        loadFileFromServer(file, false);
    }
    public void onFileDownloadSelected(@NonNull File file) {
        loadFileFromServer(file, true);
    }
    /**
     * Loads a file from the Schul-Cloud server.
     *
     * @param file     {File} - the db-saved file
     * @param download {Boolean} - whether to download the file or not
     */
    private void loadFileFromServer(@NonNull File file, boolean download) {
        sendToView(FilesMvpView::showFileDownloadStarted);
        RxUtil.unsubscribe(mFileDownloadGetterSubscription);
        mFileDownloadGetterSubscription = mDataManager
                .getFileUrl(new SignedUrlRequest(
                        SignedUrlRequest.ACTION_GET,
                        file.key,
                        file.type))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        signedUrlResponse -> {
                            Log.d("Fetched file url", signedUrlResponse.url);
                            if (download)
                                downloadFile(signedUrlResponse.url, file.name);
                            else
                                sendToView(view -> view.showFile(
                                        signedUrlResponse.url,
                                        signedUrlResponse.header.getContentType(),
                                        FileUtils.getExtension(file.name)));
                        },
                        error -> {
                            Timber.e(error, "There was an error loading file from Server.");
                            sendToView(FilesMvpView::showFileDownloadError);
                        });
    }
    /**
     * Downloads the file from the given url.
     *
     * @param url      The remote url from which the file will be downloaded
     * @param fileName The name of the downloaded file
     */
    private void downloadFile(@NonNull String url, @NonNull String fileName) {
        RxUtil.unsubscribe(mFileDownloadSubscription);
        mFileDownloadSubscription = mDataManager
                .downloadFile(url)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseBody -> sendToView(view -> view.saveFile(fileName, responseBody)),
                        error -> {
                            Timber.e(error, "There was an error loading file from Server.");
                            sendToView(FilesMvpView::showFileDownloadError);
                        });
    }

    /* File upload */
    public void onFileUploadSelected(@NonNull java.io.File file) {
        sendToView(FilesMvpView::showFileUploadStarted);
        String uploadPath = PathUtil
                .combine(mDataManager.getCurrentStorageContext(), file.getName());

        SignedUrlRequest signedUrlRequest = new SignedUrlRequest(
                SignedUrlRequest.ACTION_PUT,
                uploadPath,
                FileUtils.getMimeType(file));

        RxUtil.unsubscribe(mFileUploadGetterSubscription);
        mFileUploadGetterSubscription = mDataManager.getFileUrl(signedUrlRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        signedUrlResponse -> uploadFile(file, signedUrlResponse),
                        error -> {
                            Timber.e(error, "There was an error uploading file to Server.");
                            sendToView(FilesMvpView::showFileUploadError);
                        });
    }
    /**
     * Uploads the file to the given url.
     *
     * @param file              The file to upload
     * @param signedUrlResponse The remote url to which the file will be uploaded
     */
    private void uploadFile(@NonNull java.io.File file,
            @NonNull SignedUrlResponse signedUrlResponse) {
        RxUtil.unsubscribe(mFileUploadSubscription);
        mFileUploadSubscription = mDataManager.uploadFile(file, signedUrlResponse)
                .flatMap(responseBody -> mDataManager.persistFile(signedUrlResponse, file.getName(),
                        FileUtils.getMimeType(file), file.length()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseBody -> sendToView(view -> {
                            view.reloadFiles();
                            view.showFileUploadSuccess();
                        }),
                        error -> {
                            Timber.e(error, "There was an error uploading file from Server.");
                            sendToView(FilesMvpView::showFileUploadError);
                        });

    }

    /* File deletion */
    /**
     * Deletes a file from the server.
     *
     * @param file The file to delete
     */
    public void onFileDeleteSelected(@NonNull File file) {
        RxUtil.unsubscribe(mFileDeleteSubscription);
        mFileDeleteSubscription = mDataManager.deleteFile(file.key)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseBody -> sendToView(view -> {
                            view.reloadFiles();
                            view.showFileDeleteSuccess();
                        }),
                        error -> {
                            Timber.e(error, "There was an error deleting file from Server.");
                            sendToView(FilesMvpView::showFileDeleteError);
                        });

    }


    private void loadDirectories() {
        mDataManager.getDirectories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        directories -> sendToView(view -> view.showDirectories(directories)),
                        error -> {
                            Timber.e(error, "There was an error loading the directories.");
                            sendToView(FilesMvpView::showDirectoriesLoadError);
                        });
    }
    /* Directory change */
    /**
     * Opens a directory by fetching files for new storageContext.
     *
     * @param directory The selected directory
     */
    public void onDirectorySelected(@NonNull Directory directory) {
        onDirectorySelected(PathUtil.combine(directory.path, directory.name));
    }
    public void onDirectorySelected(@NonNull String path) {
        mDataManager.setCurrentStorageContext(path);
        loadBreadcrumbs();
        sendToView(view -> {
            view.reloadFiles();
            view.reloadDirectories();
        });
    }

    /* Directory creation */
    public void onDirectoryCreateSelected(@NonNull String name) {
        RxUtil.unsubscribe(mDirectoryCreateSubscription);
        mDirectoryCreateSubscription = mDataManager
                .createDirectory(new CreateDirectoryRequest(
                        PathUtil.combine(mDataManager.getCurrentStorageContext(), name)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        directory -> sendToView(view -> {
                            view.reloadDirectories();
                            view.showDirectoryCreateSuccess();
                        }),
                        error -> {
                            Timber.e(error,
                                    "There was an error while creating a directory on the Server.");
                            sendToView(FilesMvpView::showDirectoryCreateError);
                        });
    }

    /* Directory deletion */
    /**
     * Deletes a directory from the server.
     *
     * @param directory The directory to delete
     */
    public void onDirectoryDeleteSelected(@NonNull Directory directory) {
        RxUtil.unsubscribe(mDirectoryDeleteSubscription);
        mDirectoryDeleteSubscription = mDataManager
                .deleteDirectory(PathUtil.combine(directory.path, directory.name))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseBody -> {
                            sendToView(FilesMvpView::showDirectoryDeleteSuccess);
                            sendToView(FilesMvpView::reloadDirectories);
                        },
                        error -> {
                            Timber.e(error, "There was an error deleting directory from Server.");
                            sendToView(FilesMvpView::showDirectoryDeleteError);
                        });
    }

    /**
     * Checks the current storage path and steps back if it's not a root directory.
     *
     * @return True if stepping back was successful, false if we are already in the root directory.
     */
    public boolean onBackSelected() {
        String storageContext = mDataManager.getCurrentStorageContext();

        // first two parts are meta
        if (storageContext.split("/", 3).length > 2) {
            onDirectorySelected(PathUtil.parent(storageContext));
            return true;
        }
        return false;
    }
}

