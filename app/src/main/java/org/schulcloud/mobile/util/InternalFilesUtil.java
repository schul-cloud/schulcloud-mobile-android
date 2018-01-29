package org.schulcloud.mobile.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.ipaulpro.afilechooser.utils.FileUtils;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.injection.scope.PerActivity;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.util.dialogs.DialogFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import rx.Single;

@PerActivity
public class InternalFilesUtil {
    private static final String TAG = InternalFilesUtil.class.getCanonicalName();

    private BaseActivity mActivity;

    @Inject
    public InternalFilesUtil(@NonNull BaseActivity activity) {
        mActivity = activity;
    }

    /**
     * saves a file to local storage
     *
     * @param body     {ResponseBody} - the given file which will be saved
     * @param fileName {String} - the name of the file
     * @return whether it was successful or not
     */
    public boolean writeResponseBodyToDisk(@NonNull String fileName, @NonNull ResponseBody body) {
        String downloadDirPath = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .getAbsolutePath();
        try {
            File futureFile = new File(downloadDirPath + File.separator + fileName);
            if (!(futureFile.exists() || futureFile.createNewFile()))
                return false;

            OutputStream outputStream = new FileOutputStream(futureFile);
            outputStream.write(body.bytes());
            outputStream.close();

            DialogFactory.createSuperToast(mActivity,
                    mActivity.getString(R.string.files_fileDownload_success, fileName),
                    PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_GREEN))
                    .show();

            return true;
        } catch (IOException e) {
            Log.w(TAG, e);
            e.printStackTrace();
            DialogFactory.createSuperToast(mActivity,
                    mActivity.getResources().getString(R.string.files_fileDownload_error_save),
                    PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_RED))
                    .show();

            return false;
        }
    }


    /**
     * Opens a file chooser with a local installed file-chooser
     */
    @NonNull
    public Single<File> openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");

        return mActivity.startActivityForResult(intent)
                .map(data -> FileUtils.getFile(mActivity, data.getData()));
    }

    /**
     * Gets a file from a given uri to the devices content db
     *
     * @param contentPath {Uri} - the contentUri which references the file in the content db
     * @return a file representing the file on the device
     */
    @Deprecated
    public File getFileFromContentPath(Uri contentPath) {
        return FileUtils.getFile(mActivity, contentPath);
    }

}
