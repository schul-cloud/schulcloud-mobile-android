package org.schulcloud.mobile.data.local;

import org.schulcloud.mobile.data.model.AccessToken;
import org.schulcloud.mobile.data.model.CurrentUser;
import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.data.model.Directory;
import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.data.model.File;
import org.schulcloud.mobile.data.model.User;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import io.realm.Realm;
import rx.Observable;
import timber.log.Timber;

@Singleton
public class DatabaseHelper {

    private final Provider<Realm> mRealmProvider;

    @Inject
    DatabaseHelper(Provider<Realm> realmProvider) {
        mRealmProvider = realmProvider;
    }

    public void clearTable(Class table) {
        final Realm realm = mRealmProvider.get();
        realm.executeTransaction(realm1 -> realm1.delete(table));
    }

    /**** Users ****/

    public Observable<User> setUsers(final Collection<User> newUsers) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed()) return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(newUsers));
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

    public Observable<List<User>> getUsers() {
        final Realm realm = mRealmProvider.get();
        return realm.where(User.class).findAllAsync().asObservable()
                .filter(users -> users.isLoaded())
                .map(users -> realm.copyFromRealm(users));
    }

    public Observable<AccessToken> setAccessToken(final AccessToken newAccessToken) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed()) return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(newAccessToken));
            } catch (Exception e) {
                Timber.e(e, "There was an error while adding in Realm.");
                subscriber.onError(e);
            } finally {
                if (realm != null) {
                    subscriber.onCompleted();
                    realm.close();
                }
            }
        });
    }

    public Observable<AccessToken> getAccessToken() {
        final Realm realm = mRealmProvider.get();
        return realm.where(AccessToken.class).findFirstAsync().asObservable();
    }

    public Observable<CurrentUser> setCurrentUser(final CurrentUser currentUser) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed()) return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(currentUser));
            } catch (Exception e) {
                Timber.e(e, "There was an error while adding in Realm.");
                subscriber.onError(e);
            } finally {
                if (realm != null) {
                    subscriber.onCompleted();
                    realm.close();
                }
            }
        });
    }

    public Observable<CurrentUser> getCurrentUser() {
        final Realm realm = mRealmProvider.get();
        return realm.where(CurrentUser.class).findFirstAsync().asObservable();
    }



    /**** FileStorage ****/

    public Observable<File> setFiles(final Collection<File> files) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed()) return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(files));
            } catch (Exception e) {
                Timber.e(e, "There was an error while adding in Realm.");
                subscriber.onError(e);
            } finally {
                if (realm != null) {
                    subscriber.onCompleted();
                    realm.close();
                }
            }
        });
    }

    public Observable<List<File>> getFiles() {
        final Realm realm = mRealmProvider.get();
        return realm.where(File.class).findAllAsync().asObservable()
                .filter(files -> files.isLoaded())
                .map(files -> realm.copyFromRealm(files));
    }

    public Observable<Directory> setDirectories(final Collection<Directory> directories) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed()) return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(directories));
            } catch (Exception e) {
                Timber.e(e, "There was an error while adding in Realm.");
                subscriber.onError(e);
            } finally {
                if (realm != null) {
                    subscriber.onCompleted();
                    realm.close();
                }
            }
        });
    }

    public Observable<List<Directory>> getDirectories() {
        final Realm realm = mRealmProvider.get();
        return realm.where(Directory.class).findAllAsync().asObservable()
                .filter(directories -> directories.isLoaded())
                .map(directories -> realm.copyFromRealm(directories));
    }

    /**** Events ****/
    public Observable<Event> setEvents(final Collection<Event> events) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed()) return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(events));
            } catch (Exception e) {
                Timber.e(e, "There was an error while adding in Realm.");
                subscriber.onError(e);
            } finally {
                if (realm != null) {
                    subscriber.onCompleted();
                    realm.close();
                }
            }
        });
    }

    public Observable<List<Event>> getEvents() {
        final Realm realm = mRealmProvider.get();
        return realm.where(Event.class).findAllAsync().asObservable()
                .filter(events -> events.isLoaded())
                .map(events -> realm.copyFromRealm(events));
    }

    /**** NotificationService ****/

    public Observable<Device> setDevices(final Collection<Device> newDevices) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed()) return;
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
