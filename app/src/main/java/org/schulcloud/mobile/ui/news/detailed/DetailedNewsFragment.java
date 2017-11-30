package org.schulcloud.mobile.ui.news.detailed;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.News;
import org.schulcloud.mobile.ui.main.MainFragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailedNewsFragment extends MainFragment implements DetailedNewsMvpView {
    public static final String ARGUMENT_NEWS_ID = "ARGUMENT_NEWS_ID";

    private String mNewsId;

    @Inject
    DetailedNewsPresenter mDetailedNewsPresenter;

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.date)
    TextView date;

    /**
     * Creates a new instance of this fragment.
     *
     * @param newsId The ID of the news that should be shown.
     * @return The new instance
     */
    public static DetailedNewsFragment newInstance(@NonNull String newsId) {
        DetailedNewsFragment detailedNewsFragment = new DetailedNewsFragment();

        Bundle args = new Bundle();
        args.putString(ARGUMENT_NEWS_ID, newsId);
        detailedNewsFragment.setArguments(args);

        return detailedNewsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);

        mNewsId = getArguments().getString(ARGUMENT_NEWS_ID);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detailed_news, container, false);
        ButterKnife.bind(this, view);
        setTitle(R.string.news_news_title);

        mDetailedNewsPresenter.attachView(this);
        mDetailedNewsPresenter.loadNews(mNewsId);

        return view;
    }

    @Override
    public void showNews(News news) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        SimpleDateFormat dateFormatDeux = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date newsDate = null;
        try {
            newsDate = dateFormat.parse(news.createdAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        date.setText(dateFormatDeux.format(newsDate));
        title.setText(Html.fromHtml(news.title));
        description.setText(Html.fromHtml(news.content));
    }
}
