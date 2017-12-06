package org.schulcloud.mobile.data.datamanagers;

import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.local.TopicsDatabaseHelper;
import org.schulcloud.mobile.data.model.Contents;
import org.schulcloud.mobile.data.model.Topic;
import org.schulcloud.mobile.data.model.responseBodies.FeathersResponse;
import org.schulcloud.mobile.data.remote.RestService;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func1;

@Singleton
public class TopicDataManager {
    private final RestService mRestService;
    private final TopicsDatabaseHelper mDatabaseHelper;

    @Inject
    PreferencesHelper mPreferencesHelper;
    @Inject
    UserDataManager userDataManager;

    @Inject
    public TopicDataManager(RestService restService, PreferencesHelper preferencesHelper,
                       TopicsDatabaseHelper databaseHelper) {
        mRestService = restService;
        mPreferencesHelper = preferencesHelper;
        mDatabaseHelper = databaseHelper;
    }

    public PreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

    public Observable<Topic> syncTopics(String courseId) {
        return mRestService.getTopics(userDataManager.getAccessToken(), courseId)
                .concatMap(new Func1<FeathersResponse<Topic>, Observable<Topic>>() {
                    @Override
                    public Observable<Topic> call(FeathersResponse<Topic> topics) {
                        mDatabaseHelper.clearTable(Topic.class);
                        return mDatabaseHelper.setTopics(topics.data);
                    }
                })
                .doOnError(Throwable::printStackTrace);
    }

    public Observable<List<Topic>> getTopics() {
        return mDatabaseHelper.getTopics().distinct();
    }

    public List<Contents> getContents(String topicId) {
        return mDatabaseHelper.getContents(topicId).contents;
    }
}
