package org.schulcloud.mobile.ui.news;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.News;
import org.schulcloud.mobile.data.sync.NewsSyncService;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.news.detailed.DetailedNewsFragment;
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

    private static final String EXTRA_TRIGGER_SYNC_FLAG =
            "org.schulcloud.mobile.ui.main.MainActivity.EXTRA_TRIGGER_SYNC_FLAG";

    @Inject
    public NewsPresenter mNewsPresenter;
    @Inject
    public NewsAdapter mNewsAdapter;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefresh;

    public static Intent getStartIntent(Context context, boolean triggerDataSyncOnCreate) {
        Intent intent = new Intent(context, NewsActivity.class);
        intent.putExtra(EXTRA_TRIGGER_SYNC_FLAG, triggerDataSyncOnCreate);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_news, null, false);
        mDrawer.addView(contentView,0);
        getSupportActionBar().setTitle("News");
        ButterKnife.bind(this);
        mNewsAdapter.setContext(this);

        mRecyclerView.setAdapter(mNewsAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(
                mNewsAdapter.mContext,DividerItemDecoration.VERTICAL));
        mNewsPresenter.attachView(this);
        mNewsAdapter.mNewsPresenter.attachView(this);
        mNewsPresenter.checkSignIn(this);

        mNewsPresenter.loadNews();

        if (getIntent().getBooleanExtra(EXTRA_TRIGGER_SYNC_FLAG, true)) {
            startService(NewsSyncService.getStartIntent(this));
        }

        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.hpiRed)
                , getResources().getColor(R.color.hpiOrange)
                , getResources().getColor(R.color.hpiYellow));

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
    public void showNewsDialog(String newsId) {
        DetailedNewsFragment frag = new DetailedNewsFragment();
        Bundle args = new Bundle();
        args.putString(DetailedNewsFragment.ARGUMENT_NEWS_ID, newsId);
        frag.setArguments(args);
        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.overlay_fragment_container, frag)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void showNewsEmpty() {
        mNewsAdapter.setNews(Collections.emptyList());
        mNewsAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError() {
        DialogFactory.createGenericErrorDialog(this,
                "Leider konnten die News nicht geladen werden").show();
    }

    @Override
    public void goToSignIn() {
        Intent intent = new Intent(this, SignInActivity.class);
        this.startActivity(intent);
    }

}
