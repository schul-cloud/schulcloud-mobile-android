package org.schulcloud.mobile.ui.courses.topic;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Contents;
import org.schulcloud.mobile.ui.main.MainFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TopicFragment extends MainFragment implements TopicMvpView {
    private static final String ARGUMENT_TOPIC_ID = "ARGUMENT_TOPIC_ID";
    private static final String ARGUMENT_TOPIC_NAME = "ARGUMENT_TOPIC_NAME";

    private String mTopicId = null;
    private String mTopicName = null;

    @Inject
    TopicPresenter mTopicPresenter;

    @Inject
    ContentAdapter mContentAdapter;

    @BindView(R.id.topicName)
    TextView topicName;
    @BindView(R.id.topicRecycler)
    RecyclerView recyclerView;

    /**
     * Creates a new instance of this fragment.
     *
     * @param topicId   The ID of the topic that should be shown
     * @param topicName The name of the topic that should be shown
     * @return The new instance
     */
    public static TopicFragment newInstance(@NonNull String topicId, @NonNull String topicName) {
        TopicFragment topicFragment = new TopicFragment();

        Bundle args = new Bundle();
        args.putString(ARGUMENT_TOPIC_ID, topicId);
        args.putString(ARGUMENT_TOPIC_NAME, topicName);
        topicFragment.setArguments(args);

        return topicFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);

        mTopicId = getArguments().getString(ARGUMENT_TOPIC_ID);
        mTopicName = getArguments().getString(ARGUMENT_TOPIC_NAME);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topic, container, false);
        ButterKnife.bind(this, view);
        setTitle(R.string.courses_topic_title);

        topicName.setText(mTopicName);

        recyclerView.setAdapter(mContentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mTopicPresenter.attachView(this);
        mTopicPresenter.loadContents(mTopicId);

        return view;
    }
    @Override
    public void onPause() {
        mTopicPresenter.detachView();
        super.onPause();
    }

    /***** MVP View methods implementation *****/
    @Override
    public void showContent(@NonNull List<Contents> contents) {
        mContentAdapter.setContent(contents);
        mContentAdapter.setContext(getActivity());
    }
}
