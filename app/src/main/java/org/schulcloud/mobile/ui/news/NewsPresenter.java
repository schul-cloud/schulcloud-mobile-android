package org.schulcloud.mobile.ui.news;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@ConfigPersistent
public class NewsPresenter extends BasePresenter<NewsMvpView> {

    @Inject
    public NewsPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mSubscription);
    }

    public void loadNews() {
        checkViewAttached();
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mDataManager.getNews()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        news -> {
                            if (news.isEmpty())
                                getMvpView().showNewsEmpty();
                            else
                                getMvpView().showNews(news);
                        },
                        throwable -> {
                            Timber.e(throwable, "An error occured while loading news");
                            getMvpView().showError();
                        });
    }

    public void showNewsDetail(String newsId) {
        getMvpView().showNewsDetail(newsId);
    }
}
