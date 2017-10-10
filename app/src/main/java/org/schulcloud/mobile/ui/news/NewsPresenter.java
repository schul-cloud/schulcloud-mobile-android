package org.schulcloud.mobile.ui.news;

import android.content.Context;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.News;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.ui.base.MvpView;

import javax.inject.Inject;

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

}
