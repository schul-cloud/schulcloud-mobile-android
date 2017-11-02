package org.schulcloud.mobile.data.local;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import io.realm.Realm;

@Singleton
public class BaseDatabaseHelper {
    protected final Provider<Realm> mRealmProvider;

    @Inject
    BaseDatabaseHelper(Provider<Realm> realmProvider) {
        mRealmProvider = realmProvider;
    }

    public void clearTable(Class table) {
        final Realm realm = mRealmProvider.get();
        realm.executeTransaction(realm1 -> realm1.delete(table));
    }
    public void clearAll() {
        mRealmProvider.get().executeTransaction(realm -> realm.deleteAll());
    }
}
