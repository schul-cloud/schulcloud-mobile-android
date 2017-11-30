package org.schulcloud.mobile.ui.homework.detailed;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Comment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    @Inject
    DetailedHomeworkPresenter mDetailedHomeworkPresenter;

    private List<Comment> mComments;
    private String mUserId;

    @Inject
    public CommentsAdapter() {
        mComments = new ArrayList<>();
    }

    public void setComments(List<Comment> comments) {
        mComments = comments;
    }

    public void setUserId(String userId) { mUserId = userId; }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Comment comment = mComments.get(position);

        holder.nameTextView.setText(comment.comment);

        if (comment.author == null || !comment.author.equals(mUserId))
            holder.teacherIndicator.setFontAwesomeIcon("fa-graduation-cap");
        else
            holder.teacherIndicator.setFontAwesomeIcon("fa-user");
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_name)
        TextView nameTextView;
        @BindView(R.id.teacher)
        AwesomeTextView teacherIndicator;

        public CommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
