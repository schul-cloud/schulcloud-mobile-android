package org.schulcloud.mobile.ui.news;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.News;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.util.FormatUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

@ConfigPersistent
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<News> mNews;

    @Inject
    NewsPresenter mNewsPresenter;

    @Inject
    public NewsAdapter() {
        mNews = new ArrayList<>();
    }

    public void setNews(@NonNull List<News> news) {
        mNews = news;
        notifyDataSetChanged();
    }

    @Override
    public NewsAdapter.NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new NewsAdapter.NewsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NewsAdapter.NewsViewHolder holder, int position) {
        News news = mNews.get(position);

        holder.title.setText(news.title);

        if (news.content != null)
            holder.description.setText(Html.fromHtml(news.content));

        if (news.createdAt != null)
            holder.date.setText(FormatUtil.apiToDate(news.createdAt));

        holder.cardView.setOnClickListener(v -> mNewsPresenter.showNewsDetail(news._id));

        int pos = holder.description.getText().toString().indexOf("\n\n");
        holder.description.setText(holder.description.getText().toString()
                .substring(0, pos));
        if (!Html.fromHtml(news.content).toString().substring(pos).trim().isEmpty())
            holder.description.append("\n...");
    }

    @Override
    public int getItemCount() {
        return mNews.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.card_view)
        CardView cardView;

        public NewsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
