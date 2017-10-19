package org.schulcloud.mobile.ui.news;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.News;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by araknor on 10.10.17.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<News> mNews;
    public Context mContext;

    @Inject
    NewsPresenter mNewsPresenter;

    @Inject
    public NewsAdapter() {mNews = new ArrayList<>();}

    public void setNews(List<News> news) {mNews = news;}

    public void setContext(Context context) {mContext = context;}

    @Override
    public NewsAdapter.NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new NewsAdapter.NewsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NewsAdapter.NewsViewHolder holder, int position) {
        News news = mNews.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        SimpleDateFormat dateFormatDeux = new SimpleDateFormat("yyyy-MM-dd   HH:mm");
        Date createdDate = null;
        try {
            createdDate = dateFormat.parse(news.createdAt);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if(news.title != null) holder.nameTextView.setText(news.title);
        if(news.content != null) holder.descriptionTextView.setText(Html.fromHtml(news.content));
        if(news.createdAt != null)  holder.dateText.setText(dateFormatDeux.format(createdDate));
        holder.cardView.setOnClickListener(v -> mNewsPresenter.showNewsDialog(news._id));

        int pos = holder.descriptionTextView.getText().toString().indexOf("\n\n");
        holder.descriptionTextView.setText(holder.descriptionTextView.getText().toString()
                    .substring(0,pos));
        if(!Html.fromHtml(news.content).toString().substring(pos).trim().isEmpty()){
            holder.descriptionTextView.append("\n...");
        }
    }

    @Override
    public int getItemCount() {
        return mNews.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_newsTitle)
        TextView nameTextView;
        @BindView(R.id.text_newsDescription)
        TextView descriptionTextView;
        @BindView(R.id.text_Date)
        TextView dateText;
        @BindView(R.id.card_view)
        CardView cardView;

        public NewsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
