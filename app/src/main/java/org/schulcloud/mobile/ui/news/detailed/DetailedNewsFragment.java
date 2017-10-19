package org.schulcloud.mobile.ui.news.detailed;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.News;
import org.schulcloud.mobile.ui.base.BaseFragment;
import org.schulcloud.mobile.ui.news.NewsActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

/**
 * Created by araknor on 13.10.17.
 */

public class DetailedNewsFragment extends BaseFragment implements DetailedNewsMvpView {
    public static final String ARGUMENT_NEWS_ID = "newsId";

    private String newsId = null;

    @Inject
    DetailedNewsPresenter mDetailedNewsPresenter;

    @BindView(R.id.text_newsTitle)
    TextView newsTitle;
    @BindView(R.id.text_newsDescription)
    TextView newsDescription;
    @BindView(R.id.text_Date)
    TextView newsDateText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activityComponent().inject(this);
        View view = inflater.inflate(R.layout.fragment_detailed_news,container,false);
        ButterKnife.bind(this, view);
        Bundle args = getArguments();
        newsId = args.getString(ARGUMENT_NEWS_ID);

        mDetailedNewsPresenter.attachView(this);
        mDetailedNewsPresenter.loadNews(newsId);

        return view;
    }

    @Override
    public void showNews(News news) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        SimpleDateFormat dateFormatDeux = new SimpleDateFormat("yyyy-MM-dd   HH:mm");
        Date newsDate = null;
        try {
            newsDate = dateFormat.parse(news.createdAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(newsDateText != null) { newsDateText.setText(dateFormatDeux.format(newsDate)); }
        if(newsTitle != null) {newsTitle.setText(Html.fromHtml(news.title));}
        if(newsDescription != null) {newsDescription.setText(Html.fromHtml(news.content));}
    }

    @Override
    public void showError() {
    }

    @Override
    public void goToSignIn() {
        //neccesary?
    }
}
