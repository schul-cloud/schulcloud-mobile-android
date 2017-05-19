package org.schulcloud.mobile.ui.signin;

import org.schulcloud.mobile.ui.base.MvpView;

public interface SignInMvpView extends MvpView {
    void showSignInSuccessful();

    void showSignInFailed();
}
