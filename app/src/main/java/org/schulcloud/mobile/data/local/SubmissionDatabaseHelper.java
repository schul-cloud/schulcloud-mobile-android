package org.schulcloud.mobile.data.local;

import org.schulcloud.mobile.data.model.Submission;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import io.realm.Realm;
import rx.Observable;
import timber.log.Timber;

@Singleton
public class SubmissionDatabaseHelper extends BaseDatabaseHelper {
    @Inject
    SubmissionDatabaseHelper(Provider<Realm> realmProvider) {super(realmProvider);}

    public Observable<Submission> setSubmissions(final Collection<Submission> newSubmission) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed())
                return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(newSubmission));
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

    public Observable<List<Submission>> getSubmissions() {
        final Realm realm = mRealmProvider.get();
        return realm.where(Submission.class).findAllAsync().asObservable()
                .filter(submission -> submission.isLoaded())
                .map(submission -> realm.copyFromRealm(submission));
    }

    public Submission getSubmissionForId(String homeworkId) {
        final Realm realm = mRealmProvider.get();
        return realm.where(Submission.class).equalTo("homeworkId", homeworkId).findFirst();
    }
}
