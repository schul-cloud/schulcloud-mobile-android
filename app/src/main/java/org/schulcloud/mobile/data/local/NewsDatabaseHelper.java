package org.schulcloud.mobile.data.local;

import org.schulcloud.mobile.data.model.News;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import io.realm.Realm;
import rx.Observable;
import timber.log.Timber;

@Singleton
public class NewsDatabaseHelper extends  BaseDatabaseHelper {
    @Inject
    NewsDatabaseHelper(Provider<Realm> realmProvider) {super(realmProvider);}
    public Observable<List<News>> getNews() {
        final Realm realm = mRealmProvider.get();
        return realm.where(News.class).findAllAsync().asObservable()
                .filter(news -> news.isLoaded())
                .map(news -> {
                    List<News> newsList = realm.copyFromRealm(news);
                    Collections.sort(newsList, (o1, o2) -> o2.createdAt.compareTo(o1.createdAt));
                    return newsList;
                });
    }
    public News getNewsForId(String newsId) {
        final Realm realm = mRealmProvider.get();
        return realm.where(News.class).equalTo("_id", newsId).findFirst();
    }
    public Observable<News> setNews(final Collection<News> newNews) {
        return Observable.create(
                subscriber -> {
                    if (subscriber.isUnsubscribed())
                        return;

                    Realm realm = null;
                    try {
                        realm = mRealmProvider.get();
                        realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(newNews));
                    } catch (Exception e) {
                        Timber.e(e, "There was an error while adding in Realm.");
                        subscriber.onError(e);
                    } finally {
                        if (realm != null)
                            realm.close();
                    }
                });
    }
}
