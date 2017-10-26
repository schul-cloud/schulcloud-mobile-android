package org.schulcloud.mobile.ui.signin;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class SignInPresenter extends BasePresenter<SignInMvpView> {

    @Inject
    public SignInPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(SignInMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }


    public void signIn(String username, String password) {
        checkViewAttached();
        RxUtil.unsubscribe(mSubscription);

        mSubscription = mDataManager.signIn(username, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        accessToken -> {
                            if (accessToken == null) {
                                getMvpView().showSignInFailed();
                            } else {
                                getMvpView().showSignInSuccessful();
                            }
                        },
                        // onError
                        error -> {
                            Timber.e(error, "There was an error signing in.");
                            getMvpView().showSignInFailed();
                        },
                        // onCompleted
                        () -> getMvpView().showSignInSuccessful());
    }
}
