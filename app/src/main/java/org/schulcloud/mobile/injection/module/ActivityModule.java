package org.schulcloud.mobile.injection.module;

import android.app.Activity;
import android.content.Context;

import org.schulcloud.mobile.injection.ActivityContext;
import org.schulcloud.mobile.ui.base.BaseActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

    private BaseActivity mActivity;

    public ActivityModule(BaseActivity activity) {
        mActivity = activity;
    }

    @Provides
    Activity provideActivity() {
        return mActivity;
    }
    @Provides
    BaseActivity provideBaseActivity() {
        return mActivity;
    }

    @Provides
    @ActivityContext
    Context providesContext() {
        return mActivity;
    }
}
