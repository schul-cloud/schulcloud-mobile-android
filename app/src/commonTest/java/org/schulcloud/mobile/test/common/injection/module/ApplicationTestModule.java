package org.schulcloud.mobile.test.common.injection.module;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import org.schulcloud.mobile.data.remote.RestService;
import org.schulcloud.mobile.injection.ApplicationContext;
import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

import static org.mockito.Mockito.mock;

/**
 * Provides application-level dependencies for an app running on a testing environment
 * This allows injecting mocks if necessary.
 */
@Module
public class ApplicationTestModule {

    private final Application mApplication;

    public ApplicationTestModule(Application application) {
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
    Realm provideRealm() {
        return Realm.getDefaultInstance();
    }

    /************* MOCKS *************/

    @Provides
    @Singleton
    DataManager provideDataManager() {
        return mock(DataManager.class);
    }

    @Provides
    @Singleton
    RestService provideRestService() {
        return mock(RestService.class);
    }

}
