package org.schulcloud.mobile.ui.files;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.schulcloud.mobile.data.model.Course;
import org.schulcloud.mobile.data.model.Directory;
import org.schulcloud.mobile.data.model.File;
import org.schulcloud.mobile.ui.base.MvpView;

import java.util.List;

import okhttp3.ResponseBody;

public interface FilesMvpView extends MvpView {

    void showBreadcrumbs(@NonNull String path, @Nullable Course course);


    /* File load */
    void reloadFiles();

    void showFiles(@NonNull List<File> files);

    void showFilesLoadError();

    /* File download */
    void showFileDownloadStarted();

    void showFileDownloadError();

    void showFile(@NonNull String url, @NonNull String mimeType, @NonNull String extension);

    void saveFile(@NonNull String fileName, @NonNull ResponseBody body);

    /* File upload */
    void showCanUploadFile(boolean canCreateFile);

    void showFileUploadStarted();

    void showFileUploadSuccess();

    void showFileUploadError();

    /* File deletion */
    void showCanDeleteFiles(boolean canDeleteFiles);

    void showFileDeleteSuccess();

    void showFileDeleteError();


    /* Directory load */
    void reloadDirectories();

    void showDirectories(@NonNull List<Directory> directories);

    void showDirectoriesLoadError();

    /* Directory creation */
    void showCanCreateDirectories(boolean canCreateDirectories);

    void showDirectoryCreateSuccess();

    void showDirectoryCreateError();

    /* Directory deletion */
    void showCanDeleteDirectories(boolean canDeleteDirectories);

    void showDirectoryDeleteSuccess();

    void showDirectoryDeleteError();

}
