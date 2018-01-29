package org.schulcloud.mobile.ui.files;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Course;
import org.schulcloud.mobile.data.model.Directory;
import org.schulcloud.mobile.data.model.File;
import org.schulcloud.mobile.data.sync.DirectorySyncService;
import org.schulcloud.mobile.data.sync.FileSyncService;
import org.schulcloud.mobile.ui.main.MainFragment;
import org.schulcloud.mobile.util.dialogs.DialogFactory;
import org.schulcloud.mobile.util.dialogs.DialogUtil;
import org.schulcloud.mobile.util.InternalFilesUtil;
import org.schulcloud.mobile.util.PathUtil;
import org.schulcloud.mobile.util.ViewUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;


public class FilesFragment extends MainFragment implements FilesMvpView {
    private static final String ARGUMENT_TRIGGER_SYNC = "ARGUMENT_TRIGGER_SYNC";

    @Inject
    FilesPresenter mFilesPresenter;

    @Inject
    InternalFilesUtil mFilesUtil;
    @Inject
    FilesAdapter mFilesAdapter;
    @Inject
    DirectoriesAdapter mDirectoriesAdapter;

    private ProgressDialog mFileUploadProgressDialog;
    private ProgressDialog mFileDownloadProgressDialog;

    @BindView(R.id.files_breadcrumbs_v_color)
    View vV_breadcrumbs_color;
    @BindView(R.id.files_breadcrumbs_tv_text)
    TextView vTv_breadcrumbs_text;

    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.files_tv_empty)
    TextView vTv_empty;
    @BindView(R.id.directories_recycler_view)
    RecyclerView directoriesRecyclerView;

    @BindView(R.id.files_recycler_view)
    RecyclerView fileRecyclerView;
    @BindView(R.id.files_upload)
    FloatingActionButton fileUploadButton;

    private MenuItem nDirectoryCreate;

    @NonNull
    public static FilesFragment newInstance() {
        return newInstance(true);
    }
    /**
     * Creates a new instance of this fragment.
     *
     * @param triggerDataSyncOnCreate Allows disabling the background sync service onCreate. Should
     *                                only be set to false during testing.
     * @return The new instance
     */
    @NonNull
    public static FilesFragment newInstance(boolean triggerDataSyncOnCreate) {
        FilesFragment filesFragment = new FilesFragment();

        Bundle args = new Bundle();
        args.putBoolean(ARGUMENT_TRIGGER_SYNC, triggerDataSyncOnCreate);
        filesFragment.setArguments(args);

        return filesFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);

        if (getArguments().getBoolean(ARGUMENT_TRIGGER_SYNC, true)) {
            restartService(FileSyncService.getStartIntent(getContext()));
            restartService(DirectorySyncService.getStartIntent(getContext()));
        }

        setHasOptionsMenu(true);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_files, container, false);
        ButterKnife.bind(this, view);
        setTitle(R.string.files_title);

        fileRecyclerView.setAdapter(mFilesAdapter);
        fileRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        directoriesRecyclerView.setAdapter(mDirectoriesAdapter);
        directoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ViewUtil.initSwipeRefreshColors(swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> {
            reloadFiles();
            reloadDirectories();

            // Realm doesn't trigger a notification if a table was and stays empty, so in case of a
            // manual refresh in an empty directory the refresh indicator would never terminate.
            new Handler().postDelayed(() -> swipeRefresh.setRefreshing(false), 3000);
        });

        // Sync services are called in onCreate, so it is currently loading
        swipeRefresh.setRefreshing(true);
        // When starting the app with an empty folder selected, this lines are required
        new Handler().postDelayed(() -> {
            swipeRefresh.setRefreshing(false);
            updateEmptyText();
        }, 3000);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_files, menu);
        nDirectoryCreate = menu.findItem(R.id.files_action_directoryCreate);

        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.files_action_directoryCreate:
                createDirectory();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        mFilesPresenter.attachView(this);
    }
    @Override
    public void onPause() {
        mFilesPresenter.detachView();
        super.onPause();
    }

    @Override
    public boolean onBackPressed() {
        return mFilesPresenter.onBackSelected();
    }

    private void updateEmptyText() {
        List<Directory> directories = mDirectoriesAdapter.getDirectories();
        List<File> files = mFilesAdapter.getFiles();
        ViewUtil.setVisibility(vTv_empty, (directories == null || directories.isEmpty())
                && (files == null || files.isEmpty()));
    }

    /***** MVP View methods implementation *****/
    @Override
    public void showBreadcrumbs(@NonNull String path, @Nullable Course course) {
        String[] folders = PathUtil.getAllParts(path);
        final StringBuilder currentPath = new StringBuilder(folders[0] + "/" + folders[1]);
        SpannableStringBuilder builder = new SpannableStringBuilder();

        // Top-level directory ("Persönliche Dateien" or name and color of the course)
        if (course == null) {
            vV_breadcrumbs_color.setVisibility(View.GONE);
            builder.append(getString(R.string.filesOverview_my));
        } else {
            vV_breadcrumbs_color.setBackgroundColor(Color.parseColor(course.color));
            vV_breadcrumbs_color.setVisibility(View.VISIBLE);
            builder.append(course.name);
        }
        builder.setSpan(new BreadcrumbClickableSpan(currentPath.toString()),
                0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Add all subfolders
        for (int i = 2; i < folders.length; i++) {
            builder.append(getString(R.string.files_breadcrumbs_divider));
            currentPath.append("/").append(folders[i]);
            builder.append(folders[i]);
            builder.setSpan(new BreadcrumbClickableSpan(currentPath.toString()),
                    builder.length() - folders[i].length(), builder.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        vTv_breadcrumbs_text.setText(builder);
        vTv_breadcrumbs_text.setMovementMethod(LinkMovementMethod.getInstance());
    }
    private class BreadcrumbClickableSpan extends ClickableSpan {
        private final String mPath;

        BreadcrumbClickableSpan(@NonNull String path) {
            mPath = path;
        }

        @Override
        public void onClick(View widget) {
            mFilesPresenter.onDirectorySelected(mPath);
        }
        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }


    /* File load */
    @Override
    public void reloadFiles() {
        swipeRefresh.setRefreshing(true);
        restartService(FileSyncService.getStartIntent(getContext()));
    }
    @Override
    public void showFiles(@NonNull List<File> files) {
        mFilesAdapter.setFiles(files);
        swipeRefresh.setRefreshing(false);
        updateEmptyText();

        // adjust height of recycler view (bugfix for nested scrolling)
        ViewGroup.LayoutParams params = fileRecyclerView.getLayoutParams();

        params.height = ViewUtil.dpToPx(68) * mFilesAdapter.getItemCount()
                + ViewUtil.dpToPx(6); // Padding top
        fileRecyclerView.setLayoutParams(params);
        fileRecyclerView.setNestedScrollingEnabled(false);
    }
    @Override
    public void showFilesLoadError() {
        DialogFactory.createGenericErrorDialog(getContext(), R.string.files_fileLoad_error)
                .show();
    }

    /* File download */
    @Override
    public void showFileDownloadStarted() {
        mFileDownloadProgressDialog = DialogFactory.createProgressDialog(getContext(),
                R.string.files_fileDownload_progress);
        mFileDownloadProgressDialog.show();
    }
    @Override
    public void showFile(@NonNull String url, @NonNull String mimeType, @NonNull String extension) {
        DialogUtil.cancel(mFileDownloadProgressDialog);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(url), mimeType);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null)
            startActivity(intent);
        else
            DialogFactory.createGenericErrorDialog(getContext(),
                    getString(R.string.files_fileDownload_error_cantResolve, extension))
                    .show();
    }
    @Override
    public void saveFile(@NonNull String fileName, @NonNull ResponseBody body) {
        DialogUtil.cancel(mFileDownloadProgressDialog);

        permissionsDeniedToError(requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .subscribe(
                        results -> mFilesUtil.writeResponseBodyToDisk(fileName, body),
                        throwable -> DialogFactory.createGenericErrorDialog(getContext(),
                                R.string.files_fileDownload_error_savePermissionDenied));
    }
    @Override
    public void showFileDownloadError() {
        DialogUtil.cancel(mFileDownloadProgressDialog);
        DialogFactory.createGenericErrorDialog(getContext(), R.string.files_fileDownload_error)
                .show();
    }

    /* File upload */
    @Override
    public void showCanUploadFile(boolean canUploadFile) {
        ViewUtil.setVisibility(fileUploadButton, canUploadFile);
    }
    @OnClick(R.id.files_upload)
    void startFileUploadChoosing() {
        permissionsDeniedToError(requestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE))
                .flatMap(results -> mFilesUtil.openFileChooser())
                .subscribe(
                        file -> mFilesPresenter.onFileUploadSelected(file),
                        throwable -> DialogFactory.createGenericErrorDialog(getContext(),
                                R.string.files_fileUpload_error_readPermissionDenied)
                );
    }
    @Override
    public void showFileUploadStarted() {
        mFileUploadProgressDialog = DialogFactory.createProgressDialog(getContext(),
                R.string.files_fileUpload_progress);
        mFileUploadProgressDialog.show();

    }
    @Override
    public void showFileUploadSuccess() {
        DialogUtil.cancel(mFileUploadProgressDialog);
        DialogFactory.createSuperToast(getContext(), getString(R.string.files_fileUpload_success),
                PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_GREEN)).show();
    }
    @Override
    public void showFileUploadError() {
        DialogUtil.cancel(mFileUploadProgressDialog);
        DialogFactory.createGenericErrorDialog(getContext(), R.string.files_fileUpload_error)
                .show();
    }

    /* File deletion */
    @Override
    public void showCanDeleteFiles(boolean canDeleteFiles) {
        mFilesAdapter.setCanDeleteFiles(canDeleteFiles);
    }
    @Override
    public void showFileDeleteSuccess() {
        DialogFactory.createSuperToast(getContext(),
                getString(R.string.files_fileDelete_success),
                PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_GREEN)).show();
    }
    @Override
    public void showFileDeleteError() {
        DialogFactory.createGenericErrorDialog(getContext(), R.string.files_fileDelete_error)
                .show();
    }


    /* Directory load */
    @Override
    public void reloadDirectories() {
        swipeRefresh.setRefreshing(true);
        restartService(DirectorySyncService.getStartIntent(getContext()));
    }
    @Override
    public void showDirectories(@NonNull List<Directory> directories) {
        mDirectoriesAdapter.setDirectories(directories);
        swipeRefresh.setRefreshing(false);
        updateEmptyText();
    }
    @Override
    public void showDirectoriesLoadError() {
        DialogFactory.createGenericErrorDialog(getContext(), R.string.files_directoryLoad_error)
                .show();
    }

    /* Directory creation */
    @Override
    public void showCanCreateDirectories(boolean canCreateDirectories) {
        nDirectoryCreate.setVisible(canCreateDirectories);
    }
    private void createDirectory() {
        DialogFactory.showSimpleTextInputDialog(getContext(),
                getString(R.string.files_directoryCreate_title),
                getString(R.string.dialog_action_ok), getString(R.string.dialog_action_cancel))
                .subscribe(
                        s -> mFilesPresenter.onDirectoryCreateSelected(s),
                        throwable -> {} // Abort if cancel was selected
                );
    }
    @Override
    public void showDirectoryCreateSuccess() {
        DialogFactory.createSuperToast(getContext(),
                getString(R.string.files_directoryCreate_success),
                PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_GREEN)).show();
    }
    @Override
    public void showDirectoryCreateError() {
        DialogFactory.createGenericErrorDialog(getContext(), R.string.files_directoryCreate_error)
                .show();
    }

    /* Directory deletion */
    @Override
    public void showCanDeleteDirectories(boolean canDeleteDirectories) {
        mDirectoriesAdapter.setCanDeleteDirectories(canDeleteDirectories);
    }
    @Override
    public void showDirectoryDeleteSuccess() {
        DialogFactory.createSuperToast(getContext(),
                getString(R.string.files_directoryDelete_success),
                PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_GREEN)).show();
    }
    @Override
    public void showDirectoryDeleteError() {
        DialogFactory.createGenericErrorDialog(getContext(), R.string.files_directoryDelete_error)
                .show();
    }
}
