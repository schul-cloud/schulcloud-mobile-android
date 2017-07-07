package org.schulcloud.mobile.ui.files;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Directory;
import org.schulcloud.mobile.data.model.File;
import org.schulcloud.mobile.data.sync.DirectorySyncService;
import org.schulcloud.mobile.data.sync.FileSyncService;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.signin.SignInActivity;
import org.schulcloud.mobile.util.DialogFactory;
import org.schulcloud.mobile.util.InternalFilesUtil;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;

import static org.schulcloud.mobile.util.PermissionsUtil.checkPermissions;


public class FileActivity extends BaseActivity implements FileMvpView {

    private static final String EXTRA_TRIGGER_SYNC_FLAG =
            "org.schulcloud.mobile.ui.files.FileActivity.EXTRA_TRIGGER_SYNC_FLAG";

    private static final int FILE_CHOOSE_RESULT_ACTION = 2017;
    private static final int FILE_READER_PERMISSION_CALLBACK_ID = 44;
    private static final int FILE_WRITER_PERMISSION_CALLBACK_ID = 43;

    @Inject
    FilePresenter mFilePresenter;

    @Inject
    FilesAdapter mFilesAdapter;

    @Inject
    DirectoriesAdapter mDirectoriesAdapter;

    @BindView(R.id.directories_recycler_view)
    RecyclerView directoriesRecyclerView;

    @BindView(R.id.files_recycler_view)
    RecyclerView fileRecyclerView;

    @BindView(R.id.files_upload)
    FloatingActionButton fileUploadButton;

    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefresh;

    private InternalFilesUtil filesUtil;
    private ProgressDialog uploadProgressDialog;
    private ProgressDialog downloadProgressDialog;


    /**
     * Return an Intent to start this Activity.
     * triggerDataSyncOnCreate allows disabling the background sync service onCreate. Should
     * only be set to false during testing.
     */
    public static Intent getStartIntent(Context context, boolean triggerDataSyncOnCreate) {
        Intent intent = new Intent(context, FileActivity.class);
        intent.putExtra(EXTRA_TRIGGER_SYNC_FLAG, triggerDataSyncOnCreate);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_files, null, false);
        mDrawer.addView(contentView, 0);
        getSupportActionBar().setTitle(R.string.title_files);
        ButterKnife.bind(this);

        fileRecyclerView.setAdapter(mFilesAdapter);
        fileRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        directoriesRecyclerView.setAdapter(mDirectoriesAdapter);
        directoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        fileUploadButton.setBackgroundTintList(ColorStateList.valueOf(
                getResources().getColor(R.color.hpiRed)));
        fileUploadButton.setOnClickListener(v -> {
            this.startFileChoosing();
        });

        mFilePresenter.attachView(this);
        mFilePresenter.checkSignedIn(this);

        mFilePresenter.loadFiles();
        mFilePresenter.loadDirectories();

        if (getIntent().getBooleanExtra(EXTRA_TRIGGER_SYNC_FLAG, true)) {
            startService(FileSyncService.getStartIntent(this));
            startService(DirectorySyncService.getStartIntent(this));
        }

        filesUtil = new InternalFilesUtil(this);

        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.hpiRed), getResources().getColor(R.color.hpiOrange), getResources().getColor(R.color.hpiYellow));

        swipeRefresh.setOnRefreshListener(
                () -> {
                    startService(FileSyncService.getStartIntent(this));
                    startService(DirectorySyncService.getStartIntent(this));

                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        mFilePresenter.loadFiles();
                        mFilePresenter.loadDirectories();

                        swipeRefresh.setRefreshing(false);
                    }, 3000);
                }
        );
    }

    @Override
    protected void onDestroy() {
        mFilePresenter.detachView();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_CHOOSE_RESULT_ACTION:
                if (data != null) {
                    java.io.File file = filesUtil.getFileFromContentPath(data.getData());
                    mFilePresenter.uploadFileToServer(file);
                }
                break;

        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onBackPressed() {
        mFilePresenter.stepOneDirectoryBack();
    }

    /***** MVP View methods implementation *****/

    @Override
    public void showFiles(List<File> files) {
        mFilesAdapter.setFiles(files);
        mFilesAdapter.notifyDataSetChanged();

        // adjust height of recycler view (bugfix for nested scrolling)
        ViewGroup.LayoutParams params = fileRecyclerView.getLayoutParams();
        params.height = 250 * mFilesAdapter.getItemCount() + 200;
        fileRecyclerView.setLayoutParams(params);
        fileRecyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public void showDirectories(List<Directory> directories) {
        mDirectoriesAdapter.setDirectories(directories);
        mDirectoriesAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError() {
        DialogFactory.createGenericErrorDialog(this, getString(R.string.error_files_fetch))
                .show();
    }

    @Override
    public void showLoadingFileFromServerError() {
        DialogFactory.createGenericErrorDialog(this, R.string.error_file_load)
                .show();
    }

    @Override
    public void showFile(String url, String mimeType) {
        if (downloadProgressDialog != null && downloadProgressDialog.isShowing()) downloadProgressDialog.cancel();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(url), mimeType);
        startActivity(intent);
    }

    @Override
    public void showUploadFileError() {
        DialogFactory.createGenericErrorDialog(this, R.string.error_file_upload)
                .show();
    }

    @Override
    public void reloadFiles() {
        if (uploadProgressDialog != null && uploadProgressDialog.isShowing()) uploadProgressDialog.cancel();

        stopService(FileSyncService.getStartIntent(this));
        stopService(DirectorySyncService.getStartIntent(this));

        startService(FileSyncService.getStartIntent(this));
        startService(DirectorySyncService.getStartIntent(this));

        mFilePresenter.loadFiles();
        mFilePresenter.loadDirectories();
    }

    @Override
    public void saveFile(ResponseBody body, String fileName) {
        if (downloadProgressDialog != null && downloadProgressDialog.isShowing()) downloadProgressDialog.cancel();

        if (checkPermissions(
                FILE_WRITER_PERMISSION_CALLBACK_ID,
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            this.filesUtil.writeResponseBodyToDisk(body, fileName);
        }
    }

    @Override
    public void startFileChoosing() {
        if (checkPermissions(
                FILE_READER_PERMISSION_CALLBACK_ID,
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {


            uploadProgressDialog = DialogFactory.createProgressDialog(this, R.string.file_upload_progress);
            uploadProgressDialog.show();

            // show file chooser
            this.filesUtil.openFileChooser(FILE_CHOOSE_RESULT_ACTION);
        }
    }

    @Override
    public void startDownloading(File file, Boolean download) {
        downloadProgressDialog = DialogFactory.createProgressDialog(this, R.string.file_download_progress);
        downloadProgressDialog.show();
        mFilePresenter.loadFileFromServer(file, download);
    }

    @Override
    public void startFileDeleting(String path, String fileName) {
        DialogFactory.createSimpleOkCancelDialog(
                this,
                this.getResources().getString(R.string.delete_dialog_title),
                this.getResources().getString(R.string.file_delete_request, fileName))
                .setPositiveButton(R.string.dialog_action_ok, (dialogInterface, i) -> {
                    mFilePresenter.deleteFile(path);
                })
                .show();
    }

    @Override
    public void showFileDeleteError() {
        DialogFactory.createGenericErrorDialog(this, R.string.error_file_delete)
                .show();
    }

    @Override
    public void startDirectoryDeleting(String path, String dirName) {
        DialogFactory.createSimpleOkCancelDialog(
                this,
                this.getResources().getString(R.string.delete_dialog_title),
                this.getResources().getString(R.string.file_delete_request, dirName))
                .setPositiveButton(R.string.dialog_action_ok, (dialogInterface, i) -> {
                    mFilePresenter.deleteDirectory(path);
                })
                .show();
    }

    public void showFileDeleteSuccess() {
        DialogFactory.createSuperToast(this,
                getResources().getString(R.string.file_delete_success),
                PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_GREEN)).show();
        this.reloadFiles();
    }

    @Override
    public void showDirectoryDeleteSuccess() {
        DialogFactory.createSuperToast(this,
                getResources().getString(R.string.directory_delete_success),
                PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_GREEN)).show();
        this.reloadFiles();
    }

    @Override
    public void goToSignIn() {
        Intent intent = new Intent(this, SignInActivity.class);
        this.startActivity(intent);
    }
}
