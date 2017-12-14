package org.schulcloud.mobile.data.local;

import org.schulcloud.mobile.data.model.Topic;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import io.realm.Realm;
import rx.Observable;
import timber.log.Timber;

@Singleton
public class TopicsDatabaseHelper extends  BaseDatabaseHelper {
    @Inject
    TopicsDatabaseHelper(Provider<Realm> realmProvider) {super(realmProvider);}

    public Observable<Topic> setTopics(final Collection<Topic> newTopic) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed())
                return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(newTopic));
            } catch (Exception e) {
                Timber.e(e, "There was an error while adding in Realm.");
                subscriber.onError(e);
            } finally {
                if (realm != null) {
                    realm.close();
                }
            }
        });
    }

    public Observable<List<Topic>> getTopics() {
        final Realm realm = mRealmProvider.get();
        return realm.where(Topic.class).findAllAsync().asObservable()
                .filter(topic -> topic.isLoaded())
                .map(topic -> realm.copyFromRealm(topic));
    }

    public Topic getContents(String topicId) {
        final Realm realm = mRealmProvider.get();
        return realm.where(Topic.class).equalTo("_id", topicId).findFirst();
    }
}
