package org.schulcloud.mobile.data.local;

import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.util.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import io.realm.Realm;
import rx.Observable;
import timber.log.Timber;

@Singleton
public class HomeworkDatabaseHelper extends BaseDatabaseHelper {
    @Inject
    HomeworkDatabaseHelper(Provider<Realm> realmProvider) {super(realmProvider);}

    public Observable<Homework> setHomework(final Collection<Homework> newHomework) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed())
                return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(newHomework));
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

    public Observable<List<Homework>> getHomework() {
        final Realm realm = mRealmProvider.get();
        return realm.where(Homework.class).findAllAsync().asObservable()
                .filter(homework -> homework.isLoaded())
                .map(homework -> realm.copyFromRealm(homework));
    }

    public Homework getHomeworkForId(String homeworkId) {
        final Realm realm = mRealmProvider.get();
        return realm.where(Homework.class).equalTo("_id", homeworkId).findFirst();
    }

    public Pair<String, String> getOpenHomeworks() {
        final Realm realm = mRealmProvider.get();
        Collection<Homework> homeworks = realm.where(Homework.class).findAll();

        Date nextDueDate = null;
        int amount = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

        Calendar calendar = new GregorianCalendar(9999, 12, 31, 23, 59);
        try {
            nextDueDate = dateFormat.parse(dateFormat.format(calendar.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (Homework homework : homeworks) {
            Date untilDate = null;
            try {
                untilDate = dateFormat.parse(homework.dueDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (untilDate.after(new Date())) {
                amount++;

                if (untilDate.before(nextDueDate))
                    nextDueDate = untilDate;
            }
        }

        return new Pair<String, String>(Integer.toString(amount), dateFormat.format(nextDueDate));
    }
}
