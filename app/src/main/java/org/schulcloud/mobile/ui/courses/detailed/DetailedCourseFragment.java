package org.schulcloud.mobile.ui.courses.detailed;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Course;
import org.schulcloud.mobile.data.model.Topic;
import org.schulcloud.mobile.data.sync.TopicSyncService;
import org.schulcloud.mobile.ui.base.BaseFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailedCourseFragment extends BaseFragment implements DetailedCourseMvpView {

    private static final String EXTRA_TRIGGER_SYNC_FLAG =
            "org.schulcloud.mobile.ui.main.MainActivity.EXTRA_TRIGGER_SYNC_FLAG";

    private String courseId = null;

    @Inject
    DetailedCoursePresenter mDetailedCoursePresenter;

    @Inject
    TopicsAdapter mTopicsAdapter;

    @BindView(R.id.courseName)
    TextView courseName;
    @BindView(R.id.topicRecycler)
    RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activityComponent().inject(this);
        View view = inflater.inflate(R.layout.fragment_detailed_course, container, false);
        ButterKnife.bind(this, view);
        Bundle args = getArguments();
        courseId = args.getString("courseId");

        mRecyclerView.setAdapter(mTopicsAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mDetailedCoursePresenter.attachView(this);
        mDetailedCoursePresenter.loadCourse(courseId);
        mDetailedCoursePresenter.loadTopics();

        Intent intent = new Intent(getActivity(),TopicSyncService.class);
        intent.putExtra("courseId", courseId);
        getActivity().startService(intent);

        return view;
    }

    /***** MVP View methods implementation *****/

    @Override
    public void showCourse(Course course) {
        courseName.setText(course.name);
    }

    @Override
    public void showTopics(List<Topic> topics) {
        mTopicsAdapter.setSubmissions(topics);
        mTopicsAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError() {

    }

    @Override
    public void goToSignIn() {
        // Necessary in fragment?
    }
}
