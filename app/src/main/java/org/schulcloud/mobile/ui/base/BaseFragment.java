package org.schulcloud.mobile.ui.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.LongSparseArray;

import org.schulcloud.mobile.BuildConfig;
import org.schulcloud.mobile.SchulCloudApplication;
import org.schulcloud.mobile.injection.component.ActivityComponent;
import org.schulcloud.mobile.injection.component.ConfigPersistentComponent;
import org.schulcloud.mobile.injection.component.DaggerConfigPersistentComponent;
import org.schulcloud.mobile.injection.module.ActivityModule;
import org.schulcloud.mobile.ui.signin.SignInActivity;

import java.util.concurrent.atomic.AtomicLong;

import timber.log.Timber;

public abstract class BaseFragment extends Fragment implements MvpView {
    private static final String KEY_ACTIVITY_ID = "KEY_ACTIVITY_ID";
    private static final AtomicLong NEXT_ID = new AtomicLong(0);
    private static final LongSparseArray<ConfigPersistentComponent> sComponentsMap = new LongSparseArray<>();

    private ActivityComponent mActivityComponent;
    private long mActivityId;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivityId = savedInstanceState != null
                ? savedInstanceState.getLong(KEY_ACTIVITY_ID)
                : NEXT_ID.getAndIncrement();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            onAttachCompat(activity);
    }
    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            onAttachCompat(context);
    }

    /**
     * making it possible to inject things later, called in onAttach as there is
     * the first time a context is given.
     *
     * @param context needed to reference either the activity or the context for injection.
     */
    private void onAttachCompat(@NonNull Context context) {
        ConfigPersistentComponent configPersistentComponent;
        if (null == sComponentsMap.get(mActivityId)) {
            Timber.i("Creating new ConfigPersistentComponent id=%d", mActivityId);
            configPersistentComponent = DaggerConfigPersistentComponent.builder()
                    .applicationComponent(SchulCloudApplication.get(getActivity()).getComponent())
                    .build();
            sComponentsMap.put(mActivityId, configPersistentComponent);
        } else {
            Timber.i("Reusing ConfigPersistentComponent id=%d", mActivityId);
            configPersistentComponent = sComponentsMap.get(mActivityId);
        }
        mActivityComponent = configPersistentComponent
                .activityComponent(new ActivityModule(getActivity()));
    }

    public ActivityComponent activityComponent() {
        return mActivityComponent;
    }

    @Override
    public void goToSignIn() {
        startActivity(new Intent(getContext(), SignInActivity.class));
    }
}
