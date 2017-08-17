package org.schulcloud.mobile.ui.courses.detailed;

import android.app.FragmentManager;
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
    public static final String ARGUMENT_COURSE_ID = "courseId";

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
        courseId = args.getString(ARGUMENT_COURSE_ID);

        mRecyclerView.setAdapter(mTopicsAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mDetailedCoursePresenter.attachView(this);
        mDetailedCoursePresenter.loadCourse(courseId);
        mDetailedCoursePresenter.loadTopics();

        Intent intent = new Intent(getActivity(), TopicSyncService.class);
        intent.putExtra(TopicSyncService.ARGUMENT_COURSE_ID, courseId);
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
        mTopicsAdapter.setTopics(topics);
        mTopicsAdapter.notifyDataSetChanged();
    }

    @Override
    public void showTopicFragment(String topicId, String topicName) {
        TopicFragment frag = new TopicFragment();
        Bundle args = new Bundle();
        args.putString(TopicFragment.ARGUMENT_TOPIC_ID, topicId);
        args.putString(TopicFragment.ARGUMENT_TOPIC_NAME, topicName);
        frag.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.overlay_fragment_container, frag)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void showError() {

    }

    @Override
    public void goToSignIn() {
        // Necessary in fragment?
    }
}
