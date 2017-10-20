package org.schulcloud.mobile.ui.news;

import android.content.Context;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.News;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.ui.base.MvpView;
import org.schulcloud.mobile.util.RxUtil;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * Created by araknor on 10.10.17.
 */

public class NewsPresenter extends BasePresenter<NewsMvpView> {
    @Inject
    public NewsPresenter(DataManager dataManager) {mDataManager = dataManager;}

    @Override
    public void attachView(NewsMvpView mvpView) {super.attachView(mvpView);}

    @Override
    public void detachView() {
        super.detachView();
        if(mSubscription != null) mSubscription.unsubscribe();
    }

    public void checkSignIn(Context context) {super.isAlreadySignedIn(mDataManager,context);}

    public void loadNews() {
        checkViewAttached();
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mDataManager.getNews()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<News>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e,"Es gab einen Fehler beim Laden der News");
                        getMvpView().showError();
                    }

                    @Override
                    public void onNext(List<News> newses) {
                        if(newses.isEmpty()){
                            getMvpView().showNewsEmpty();
                        } else {
                            getMvpView().showNews(newses);
                        }
                    }
                });
    };

    public void showNewsDialog(String newsId) {
        getMvpView().showNewsDialog(newsId);
    }
}
