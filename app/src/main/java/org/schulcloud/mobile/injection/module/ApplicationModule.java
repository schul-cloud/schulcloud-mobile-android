package org.schulcloud.mobile.injection.module;

import android.app.Application;
import android.content.Context;

import org.schulcloud.mobile.injection.ApplicationContext;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Provide application-level dependencies.
 */
@Module
public class ApplicationModule {
    private final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication;
    }

    @Provides
    @Singleton
    RealmConfiguration provideRealmConfiguration(@ApplicationContext Context context) {
        Realm.init(context);
        RealmConfiguration.Builder builder = new RealmConfiguration.Builder();
        builder.name("default.realm");
        return builder.build();
    }

    @Provides
    Realm provideRealm(RealmConfiguration realmConfiguration) {
        return Realm.getInstance(realmConfiguration);
    }
}
