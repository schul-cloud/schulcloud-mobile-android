package org.schulcloud.mobile.ui.news.detailed;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.News;
import org.schulcloud.mobile.ui.base.BasePresenter;

import javax.inject.Inject;

/**
 * Created by araknor on 13.10.17.
 */

public class DetailedNewsPresenter extends BasePresenter<DetailedNewsMvpView> {

    @Inject
    public DetailedNewsPresenter (DataManager dataManager) {mDataManager = dataManager;}

    @Override
    public void attachView(DetailedNewsMvpView detailedNewsMvpView) {super.attachView(detailedNewsMvpView);}

    @Override
    public void detachView() {
        super.detachView();
        if(mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadNews(String newsId) {
        checkViewAttached();
        getMvpView().showNews(mDataManager.getNewsForId(newsId));
    }
}
