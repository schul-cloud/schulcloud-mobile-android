package org.schulcloud.mobile.data.local;

import org.schulcloud.mobile.data.model.Directory;
import org.schulcloud.mobile.data.model.File;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import io.realm.Realm;
import rx.Observable;
import timber.log.Timber;

public class FileStorageDatabasehelper extends BaseDatabaseHelper{

    @Inject
    FileStorageDatabasehelper(Provider<Realm> realmProvider) {super(realmProvider);}

    public Observable<File> setFiles(final Collection<File> files) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed())
                return;
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
            if (subscriber.isUnsubscribed())
                return;
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
}
