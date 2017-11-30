package org.schulcloud.mobile.ui.news;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.News;
import org.schulcloud.mobile.data.sync.NewsSyncService;
import org.schulcloud.mobile.ui.main.MainFragment;
import org.schulcloud.mobile.ui.news.detailed.DetailedNewsFragment;
import org.schulcloud.mobile.util.DialogFactory;
import org.schulcloud.mobile.util.ViewUtil;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsFragment extends MainFragment implements NewsMvpView {
    private static final String ARGUMENT_TRIGGER_SYNC = "ARGUMENT_TRIGGER_SYNC";

    @Inject
    public NewsPresenter mNewsPresenter;

    @Inject
    public NewsAdapter mNewsAdapter;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefresh;

    public static NewsFragment newInstance() {
        return newInstance(true);
    }
    /**
     * Creates a new instance of this fragment.
     *
     * @param triggerDataSyncOnCreate Allows disabling the background sync service onCreate. Should
     *                                only be set to false during testing.
     * @return The new instance
     */
    public static NewsFragment newInstance(boolean triggerDataSyncOnCreate) {
        NewsFragment courseFragment = new NewsFragment();

        Bundle args = new Bundle();
        args.putBoolean(ARGUMENT_TRIGGER_SYNC, triggerDataSyncOnCreate);
        courseFragment.setArguments(args);

        return courseFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);

        if (getArguments().getBoolean(ARGUMENT_TRIGGER_SYNC, true))
            startService(NewsSyncService.getStartIntent(getContext()));
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.bind(this, view);
        setTitle(R.string.news_title);

        mRecyclerView.setAdapter(mNewsAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        ViewUtil.initSwipeRefreshColors(swipeRefresh);
        swipeRefresh.setOnRefreshListener(
                () -> {
                    startService(NewsSyncService.getStartIntent(getContext()));

                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        mNewsPresenter.loadNews();

                        swipeRefresh.setRefreshing(false);
                    }, 3000);
                }
        );

        mNewsPresenter.attachView(this);
        mNewsPresenter.loadNews();

        return view;
    }
    @Override
    public void onDestroy() {
        mNewsPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void showNews(List<News> news) {
        mNewsAdapter.setNews(news);
        mNewsAdapter.notifyDataSetChanged();
    }
    @Override
    public void showNewsEmpty() {
        mNewsAdapter.setNews(Collections.emptyList());
        mNewsAdapter.notifyDataSetChanged();
    }
    @Override
    public void showError() {
        DialogFactory.createGenericErrorDialog(getContext(), R.string.news_loading_error).show();
    }

    @Override
    public void showNewsDetail(String newsId) {
        addFragment(DetailedNewsFragment.newInstance(newsId));
    }
}
