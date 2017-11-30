package org.schulcloud.mobile.ui.courses.topic;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.Contents;
import org.schulcloud.mobile.ui.courses.detailed.DetailedCoursePresenter;
import org.schulcloud.mobile.util.PicassoImageGetter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {

    private List<Contents> mContent;
    private String mUserId;
    private Context mContext;

    @Inject
    DetailedCoursePresenter mDetailedCoursePresenter;

    @Inject
    DataManager mDataManger;

    @Inject
    public ContentAdapter() {
        mContent = new ArrayList<>();
    }

    public void setContent(List<Contents> content) {
        mContent = content;
    }

    public void setContext(Context context) { mContext = context; }

    @Override
    public ContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_content, parent, false);
        return new ContentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContentViewHolder holder, int position) {
        Contents contents = mContent.get(position);
        holder.nameTextView.setText(contents.title);

        PicassoImageGetter imageGetter = new PicassoImageGetter(holder.descriptionTextView, mContext, mDataManger.getAccessToken());

        if (contents.component.equals("text"))
            holder.descriptionTextView.setText(Html.fromHtml(contents.content.text, imageGetter, null));
        else
            holder.descriptionTextView.setText(mContext.getString(R.string.courses_content_error_notSupported, contents.component));
    }

    @Override
    public int getItemCount() {
        return mContent.size();
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_name)
        TextView nameTextView;
        @BindView(R.id.text_description)
        TextView descriptionTextView;

        public ContentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
