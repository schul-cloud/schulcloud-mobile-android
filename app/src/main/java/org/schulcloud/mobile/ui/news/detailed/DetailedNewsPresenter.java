package org.schulcloud.mobile.ui.news.detailed;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

public class DetailedNewsPresenter extends BasePresenter<DetailedNewsMvpView> {

    @Inject
    public DetailedNewsPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mSubscription);
    }

    public void loadNews(String newsId) {
        checkViewAttached();
        getMvpView().showNews(mDataManager.getNewsForId(newsId));
    }
}
