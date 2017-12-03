package org.schulcloud.mobile.ui.signin;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class SignInPresenter extends BasePresenter<SignInMvpView> {
    @Inject
    UserDataManager mUserDataManager;

    @Inject
    public SignInPresenter(UserDataManager userDataManager) {
        mUserDataManager = userDataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mSubscription);
    }

    public void signIn(String username, String password) {
        checkViewAttached();
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mUserDataManager.signIn(username, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        accessToken -> {},
                        throwable -> {
                            Timber.e(throwable, "There was an error signing in.");
                            getMvpView().showSignInFailed();
                        },
                        () -> getMvpView().showSignInSuccessful());
    }
}
