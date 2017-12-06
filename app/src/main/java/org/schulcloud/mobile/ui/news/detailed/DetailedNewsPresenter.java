package org.schulcloud.mobile.ui.news.detailed;

import org.schulcloud.mobile.data.datamanagers.NewsDataManager;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

public class DetailedNewsPresenter extends BasePresenter<DetailedNewsMvpView> {

    @Inject
    NewsDataManager mNewsDataManager;

    @Inject
    public DetailedNewsPresenter(NewsDataManager newsDataManager) {
        mNewsDataManager = newsDataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mSubscription);
    }

    public void loadNews(String newsId) {
        checkViewAttached();
        getMvpView().showNews(mNewsDataManager.getNewsForId(newsId));
    }
}
