package org.schulcloud.mobile.ui.news;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.News;
import org.schulcloud.mobile.data.sync.CourseSyncService;
import org.schulcloud.mobile.data.sync.NewsSyncService;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.homework.HomeworkPresenter;
import org.schulcloud.mobile.ui.signin.SignInActivity;
import org.schulcloud.mobile.util.DialogFactory;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by araknor on 10.10.17.
 */

public class NewsActivity extends BaseActivity implements NewsMvpView {

    @Inject
    public NewsPresenter mNewsPresenter;
    @Inject
    public NewsAdapter mNewsAdapter;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefresh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_news, null, false);
        mDrawer.addView(contentView,0);
        getSupportActionBar().setTitle("News");
        ButterKnife.bind(this);


        mRecyclerView.setAdapter(mNewsAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mNewsPresenter.attachView(this);
        mNewsPresenter.checkSignIn(this);

        mNewsPresenter.loadNews();

        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.hpiRed), getResources().getColor(R.color.hpiOrange), getResources().getColor(R.color.hpiYellow));

        swipeRefresh.setOnRefreshListener(
                () -> {
                    startService(NewsSyncService.getStartIntent(this));

                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        mNewsPresenter.loadNews();

                        swipeRefresh.setRefreshing(false);
                    }, 3000);
                }
        );

    }

    @Override
    public void onDestroy(){
        mNewsPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void showNews(List<News> newses) {
        mNewsAdapter.setNews(newses);
        mNewsAdapter.setContext(this);
        mNewsAdapter.notifyDataSetChanged();
    }

    @Override
    public void showNewsDialog(String newsId){

    }

    @Override
    public void showNewsEmpty() {
        mNewsAdapter.setNews(Collections.emptyList());
        mNewsAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError() {
        DialogFactory.createGenericErrorDialog(this,"Leider konnten die News nicht geladen werden").show();
    }

    @Override
    public void goToSignIn() {
        Intent intent = new Intent(this, SignInActivity.class);
        this.startActivity(intent);
    }

}
