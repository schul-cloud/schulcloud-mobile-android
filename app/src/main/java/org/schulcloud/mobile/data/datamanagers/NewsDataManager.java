package org.schulcloud.mobile.data.datamanagers;

import org.schulcloud.mobile.data.local.DatabaseHelper;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.model.News;
import org.schulcloud.mobile.data.remote.RestService;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by araknor on 01.12.17.
 */

@Singleton
public class NewsDataManager {

    private final RestService mRestService;
    private final DatabaseHelper mDatabaseHelper;

    @Inject
    PreferencesHelper mPreferencesHelper;
    @Inject
    UserDataManager userDataManager;

    @Inject
    public NewsDataManager(RestService restService, PreferencesHelper preferencesHelper,
                               DatabaseHelper databaseHelper) {
        mRestService = restService;
        mPreferencesHelper = preferencesHelper;
        mDatabaseHelper = databaseHelper;
    }

    public PreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

    public Observable<List<News>> getNews() {
        return mDatabaseHelper.getNews();
    }
    public News getNewsForId(String newsId) {
        return mDatabaseHelper.getNewsForId(newsId);
    }
    public Observable<News> syncNews() {
        return mRestService.getNews(userDataManager.getAccessToken())
                .concatMap(newsFeathersResponse -> {
                    mDatabaseHelper.clearTable(News.class);
                    return mDatabaseHelper.setNews(newsFeathersResponse.data);
                }).doOnError(Throwable::printStackTrace);
    }
}
