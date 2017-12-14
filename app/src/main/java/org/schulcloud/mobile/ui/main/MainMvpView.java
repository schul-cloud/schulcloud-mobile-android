package org.schulcloud.mobile.ui.main;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.ui.base.MvpView;

public interface MainMvpView extends MvpView {

    MainFragment[] getInitialFragments();

    void showFragment(@NonNull MainFragment fragment, int oldTabIndex, int newTabIndex);

    void finish();

}
