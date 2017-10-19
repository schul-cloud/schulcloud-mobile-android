package org.schulcloud.mobile.injection.component;

import android.app.Application;
import android.content.Context;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.local.DatabaseHelper;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.remote.RestService;
import org.schulcloud.mobile.data.sync.CourseSyncService;
import org.schulcloud.mobile.data.sync.DeviceSyncService;
import org.schulcloud.mobile.data.sync.DirectorySyncService;
import org.schulcloud.mobile.data.sync.EventSyncService;
import org.schulcloud.mobile.data.sync.FileSyncService;
import org.schulcloud.mobile.data.sync.HomeworkSyncService;
import org.schulcloud.mobile.data.sync.NewsSyncService;
import org.schulcloud.mobile.data.sync.SubmissionSyncService;
import org.schulcloud.mobile.data.sync.TopicSyncService;
import org.schulcloud.mobile.data.sync.UserSyncService;
import org.schulcloud.mobile.injection.ApplicationContext;
import org.schulcloud.mobile.injection.module.ApplicationModule;
import org.schulcloud.mobile.injection.module.RestModule;
import org.schulcloud.mobile.util.RxEventBus;
import org.schulcloud.mobile.util.firebase.MessagingService;

import javax.inject.Singleton;

import dagger.Component;
import io.realm.Realm;

@Singleton
@Component(modules = {ApplicationModule.class, RestModule.class})
public interface ApplicationComponent {

    void inject(UserSyncService userSyncService);

    void inject(FileSyncService fileSyncService);

    void inject(DirectorySyncService directorySyncService);

    void inject(EventSyncService eventSyncService);

    void inject(DeviceSyncService deviceSyncService);

    void inject(MessagingService messagingService);

    void inject(HomeworkSyncService homeworkSyncService);

    void inject(SubmissionSyncService submissionSyncService);

    void inject(CourseSyncService courseSyncService);

    void inject(TopicSyncService topicSyncService);

    void inject(NewsSyncService newsSyncService);

    @ApplicationContext
    Context context();

    Application application();

    RestService restService();

    PreferencesHelper preferencesHelper();

    DatabaseHelper databaseHelper();

    Realm realm();

    DataManager dataManager();

    RxEventBus eventBus();
}
