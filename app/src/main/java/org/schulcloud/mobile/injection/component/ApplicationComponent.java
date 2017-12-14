package org.schulcloud.mobile.injection.component;

import android.app.Application;
import android.content.Context;

import org.schulcloud.mobile.data.datamanagers.CourseDataManager;
import org.schulcloud.mobile.data.datamanagers.EventDataManager;
import org.schulcloud.mobile.data.datamanagers.FeedbackDataManager;
import org.schulcloud.mobile.data.datamanagers.FileDataManager;
import org.schulcloud.mobile.data.datamanagers.HomeworkDataManager;
import org.schulcloud.mobile.data.datamanagers.NewsDataManager;
import org.schulcloud.mobile.data.datamanagers.NotificationDataManager;
import org.schulcloud.mobile.data.datamanagers.SubmissionDataManager;
import org.schulcloud.mobile.data.datamanagers.TopicDataManager;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.data.local.BaseDatabaseHelper;
import org.schulcloud.mobile.data.local.CourseDatabaseHelper;
import org.schulcloud.mobile.data.local.EventsDatabaseHelper;
import org.schulcloud.mobile.data.local.FileStorageDatabasehelper;
import org.schulcloud.mobile.data.local.HomeworkDatabaseHelper;
import org.schulcloud.mobile.data.local.NewsDatabaseHelper;
import org.schulcloud.mobile.data.local.NotificationsDatabaseHelper;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.local.SubmissionDatabaseHelper;
import org.schulcloud.mobile.data.local.TopicsDatabaseHelper;
import org.schulcloud.mobile.data.local.UserDatabaseHelper;
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

    BaseDatabaseHelper baseDatabaseHelper();

    CourseDatabaseHelper courseDatabaseHelper();

    EventsDatabaseHelper eventsDatabaseHelper();

    FileStorageDatabasehelper fileStorageDatabaseHelper();

    HomeworkDatabaseHelper homeworkDatabaseHelper();

    NewsDatabaseHelper newsDatabaseHelper();

    NotificationsDatabaseHelper notificationDatabaseHelper();

    SubmissionDatabaseHelper submissionDatabaseHelper();

    TopicsDatabaseHelper topicsDatabaseHelper();

    UserDatabaseHelper userDataBaseHelper();

    Realm realm();

    CourseDataManager courseDataManager();

    EventDataManager eventDataManager();

    FeedbackDataManager feedbackDataManager();

    FileDataManager fileDataManager();

    HomeworkDataManager homeworkdDataManager();

    NewsDataManager newsDataManager();

    NotificationDataManager notificationDataManager();

    SubmissionDataManager submissionDataManager();

    TopicDataManager topicDataManager();

    UserDataManager userDataManager();

    RxEventBus eventBus();
}
