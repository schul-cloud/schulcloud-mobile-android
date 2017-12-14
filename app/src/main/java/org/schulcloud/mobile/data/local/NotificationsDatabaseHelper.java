package org.schulcloud.mobile.data.local;

import org.schulcloud.mobile.data.model.Device;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import io.realm.Realm;
import rx.Observable;
import timber.log.Timber;

@Singleton
public class NotificationsDatabaseHelper extends  BaseDatabaseHelper {
    @Inject
    NotificationsDatabaseHelper(Provider<Realm> realmProvider) {super(realmProvider);}
    public Observable<Device> setDevices(final Collection<Device> newDevices) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed())
                return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(newDevices));
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

    public Observable<List<Device>> getDevices() {
        final Realm realm = mRealmProvider.get();
        return realm.where(Device.class).findAllAsync().asObservable()
                .filter(devices -> devices.isLoaded())
                .map(devices -> realm.copyFromRealm(devices));
    }
}
