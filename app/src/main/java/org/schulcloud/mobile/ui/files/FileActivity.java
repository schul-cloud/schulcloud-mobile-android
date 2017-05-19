package org.schulcloud.mobile.ui.files;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Directory;
import org.schulcloud.mobile.data.model.File;
import org.schulcloud.mobile.data.sync.DirectorySyncService;
import org.schulcloud.mobile.data.sync.FileSyncService;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.signin.SignInActivity;
import org.schulcloud.mobile.util.DialogFactory;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FileActivity extends BaseActivity implements FileMvpView {

    private static final String EXTRA_TRIGGER_SYNC_FLAG =
            "org.schulcloud.mobile.ui.files.FileActivity.EXTRA_TRIGGER_SYNC_FLAG";

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
        ButterKnife.bind(this);

        fileRecyclerView.setAdapter(mFilesAdapter);
        fileRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        directoriesRecyclerView.setAdapter(mDirectoriesAdapter);
        directoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mFilePresenter.attachView(this);
        mFilePresenter.checkSignedIn();

        mFilePresenter.loadFiles();
        mFilePresenter.loadDirectories();

        if (getIntent().getBooleanExtra(EXTRA_TRIGGER_SYNC_FLAG, true)) {
            startService(FileSyncService.getStartIntent(this));
            startService(DirectorySyncService.getStartIntent(this));
        }
    }

    @Override
    protected void onDestroy() {
        mFilePresenter.detachView();
        super.onDestroy();
    }

    /***** MVP View methods implementation *****/

    @Override
    public void showFiles(List<File> files) {
        mFilesAdapter.setFiles(files);
        mFilesAdapter.notifyDataSetChanged();
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
    public void goToSignIn() {
        Intent intent = new Intent(this, SignInActivity.class);
        this.startActivity(intent);
    }
}
