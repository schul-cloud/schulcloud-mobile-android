package org.schulcloud.mobile;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;

import org.schulcloud.mobile.injection.component.ApplicationComponent;
import org.schulcloud.mobile.injection.component.DaggerApplicationComponent;
import org.schulcloud.mobile.injection.module.ApplicationModule;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class SchulCloudApplication extends Application  {

    ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            Fabric.with(this, new Crashlytics());
        }
    }

    public static SchulCloudApplication get(Context context) {
        return (SchulCloudApplication) context.getApplicationContext();
    }

    public ApplicationComponent getComponent() {
        if (mApplicationComponent == null) {
            mApplicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
        }
        return mApplicationComponent;
    }

    // Needed to replace the component with a test specific one
    public void setComponent(ApplicationComponent applicationComponent) {
        mApplicationComponent = applicationComponent;
    }
}
