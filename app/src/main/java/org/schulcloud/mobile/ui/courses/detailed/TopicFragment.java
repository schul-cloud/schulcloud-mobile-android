package org.schulcloud.mobile.ui.courses.detailed;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Contents;
import org.schulcloud.mobile.ui.base.BaseFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TopicFragment extends BaseFragment implements TopicMvpView {
    private static final String EXTRA_TRIGGER_SYNC_FLAG =
            "org.schulcloud.mobile.ui.main.MainActivity.EXTRA_TRIGGER_SYNC_FLAG";
    public static final String ARGUMENT_TOPIC_ID = "topicId";
    public static final String ARGUMENT_TOPIC_NAME = "topicName";

    private String topicId = null;

    @Inject
    TopicPresenter mTopicPresenter;

    @Inject
    ContentAdapter mContentAdapter;

    @BindView(R.id.topicName)
    TextView topicName;
    @BindView(R.id.topicRecycler)
    RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activityComponent().inject(this);
        View view = inflater.inflate(R.layout.fragment_topic, container, false);
        ButterKnife.bind(this, view);
        Bundle args = getArguments();
        topicId = args.getString(ARGUMENT_TOPIC_ID);

        topicName.setText(args.getString(ARGUMENT_TOPIC_NAME));

        mRecyclerView.setAdapter(mContentAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mTopicPresenter.attachView(this);
        mTopicPresenter.loadContents(topicId);

        return view;
    }

    /***** MVP View methods implementation *****/

    @Override
    public void showContent(List<Contents> contents) {
        mContentAdapter.setContent(contents);
        mContentAdapter.setContext(getActivity());
        mContentAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError() {

    }

    @Override
    public void goToSignIn() {
        // Necessary in fragment?
    }
}
