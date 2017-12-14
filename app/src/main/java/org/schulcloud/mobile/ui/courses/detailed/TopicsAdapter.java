package org.schulcloud.mobile.ui.courses.detailed;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Topic;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.TopicsViewHolder> {

    private List<Topic> mTopics;

    @Inject
    DetailedCoursePresenter mDetailedCoursePresenter;

    @Inject
    public TopicsAdapter() {
        mTopics = new ArrayList<>();
    }

    public void setTopics(List<Topic> topics) {
        mTopics = topics;
    }

    @Override
    public TopicsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_topic, parent, false);
        return new TopicsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TopicsViewHolder holder, int position) {
        Topic topic = mTopics.get(position);

        holder.nameTextView.setText(topic.name);

        holder.cardView.setOnClickListener(
                v -> mDetailedCoursePresenter.showTopicDetail(topic._id, topic.name));
    }

    @Override
    public int getItemCount() {
        return mTopics.size();
    }

    class TopicsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_name)
        TextView nameTextView;
        @BindView(R.id.card_view)
        CardView cardView;

        public TopicsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
