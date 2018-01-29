package org.schulcloud.mobile.ui.files.overview;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Course;
import org.schulcloud.mobile.data.sync.CourseSyncService;
import org.schulcloud.mobile.ui.files.FilesFragment;
import org.schulcloud.mobile.ui.main.MainFragment;
import org.schulcloud.mobile.util.ViewUtil;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FileOverviewFragment extends MainFragment implements FileOverviewMvpView {
    private static final String ARGUMENT_TRIGGER_SYNC = "ARGUMENT_TRIGGER_SYNC";

    @Inject
    FileOverviewPresenter mFileOverviewPresenter;

    @Inject
    CourseDirectoryAdapter mCourseDirectoryAdapter;

    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout vSwipeRefresh;

    @BindView(R.id.filesOverview_my_c_wrapper)
    CardView vC_myWrapper;

    @BindView(R.id.filesOverview_courses_rv_courses)
    RecyclerView vRv_coursesList;
    @BindView(R.id.filesOverview_courses_tv_coursesError)
    TextView vTv_coursesError;

    @NonNull
    public static FileOverviewFragment newInstance() {
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
    public static FileOverviewFragment newInstance(boolean triggerDataSyncOnCreate) {
        FileOverviewFragment fileOverviewFragment = new FileOverviewFragment();

        Bundle args = new Bundle();
        args.putBoolean(ARGUMENT_TRIGGER_SYNC, triggerDataSyncOnCreate);
        fileOverviewFragment.setArguments(args);

        return fileOverviewFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);

        if (getArguments().getBoolean(ARGUMENT_TRIGGER_SYNC, true))
            startService(CourseSyncService.getStartIntent(getContext()));
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_overview, container, false);
        ButterKnife.bind(this, view);
        setTitle(R.string.files_title);

        ViewUtil.initSwipeRefreshColors(vSwipeRefresh);
        vSwipeRefresh.setOnRefreshListener(
                () -> {
                    startService(CourseSyncService.getStartIntent(getContext()));

                    new Handler().postDelayed(() -> {
                        mFileOverviewPresenter.load();

                        vSwipeRefresh.setRefreshing(false);
                    }, 3000);
                }
        );

        vC_myWrapper.setOnClickListener(v -> mFileOverviewPresenter.showMyFiles());

        vRv_coursesList.setAdapter(mCourseDirectoryAdapter);
        vRv_coursesList.setLayoutManager(new LinearLayoutManager(getContext()));

        mFileOverviewPresenter.attachView(this);
        mFileOverviewPresenter.load();

        return view;
    }
    @Override
    public void onPause() {
        mFileOverviewPresenter.detachView();
        super.onPause();
    }

    /***** MVP View methods implementation *****/
    @Override
    public void showCourses(@NonNull List<Course> courses) {
        vRv_coursesList.setVisibility(View.VISIBLE);
        vTv_coursesError.setVisibility(View.GONE);
        mCourseDirectoryAdapter.setCourses(courses);
    }
    @Override
    public void showCoursesError() {
        vRv_coursesList.setVisibility(View.GONE);
        vTv_coursesError.setVisibility(View.VISIBLE);
    }

    @Override
    public void showDirectory() {
        addFragment(FilesFragment.newInstance());
    }
}
