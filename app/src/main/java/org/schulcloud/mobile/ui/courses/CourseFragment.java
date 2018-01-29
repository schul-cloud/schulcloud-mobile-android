package org.schulcloud.mobile.ui.courses;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Course;
import org.schulcloud.mobile.data.sync.CourseSyncService;
import org.schulcloud.mobile.ui.courses.detailed.DetailedCourseFragment;
import org.schulcloud.mobile.ui.main.MainFragment;
import org.schulcloud.mobile.util.dialogs.DialogFactory;
import org.schulcloud.mobile.util.ViewUtil;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CourseFragment extends MainFragment implements CourseMvpView {
    private static final String ARGUMENT_TRIGGER_SYNC = "ARGUMENT_TRIGGER_SYNC";

    @Inject
    CoursePresenter mCoursePresenter;

    @Inject
    CourseAdapter mCourseAdapter;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefresh;

    public static CourseFragment newInstance() {
        return newInstance(true);
    }
    /**
     * Creates a new instance of this fragment.
     *
     * @param triggerDataSyncOnCreate Allows disabling the background sync service onCreate. Should
     *                                only be set to false during testing.
     * @return The new instance
     */
    public static CourseFragment newInstance(boolean triggerDataSyncOnCreate) {
        CourseFragment courseFragment = new CourseFragment();

        Bundle args = new Bundle();
        args.putBoolean(ARGUMENT_TRIGGER_SYNC, triggerDataSyncOnCreate);
        courseFragment.setArguments(args);

        return courseFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);

        if (getArguments().getBoolean(ARGUMENT_TRIGGER_SYNC, true))
            startService(CourseSyncService.getStartIntent(getContext()));
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course, container, false);
        ButterKnife.bind(this, view);
        setTitle(R.string.courses_title);

        recyclerView.setAdapter(mCourseAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ViewUtil.initSwipeRefreshColors(swipeRefresh);
        swipeRefresh.setOnRefreshListener(
                () -> {
                    startService(CourseSyncService.getStartIntent(getContext()));

                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        mCoursePresenter.loadCourses();

                        swipeRefresh.setRefreshing(false);
                    }, 3000);
                }
        );

        mCoursePresenter.attachView(this);
        mCoursePresenter.loadCourses();

        return view;
    }
    @Override
    public void onPause() {
        mCoursePresenter.detachView();
        super.onPause();
    }

    /***** MVP View methods implementation *****/
    @Override
    public void showCourses(List<Course> courses) {
        mCourseAdapter.setCourses(courses);
    }
    @Override
    public void showError() {
        DialogFactory.createGenericErrorDialog(getContext(), R.string.courses_loading_error).show();
    }

    @Override
    public void showCoursesEmpty() {
        mCourseAdapter.setCourses(Collections.emptyList());
    }

    @Override
    public void showCourseDetail(String courseId) {
        addFragment(DetailedCourseFragment.newInstance(courseId));
    }
}
