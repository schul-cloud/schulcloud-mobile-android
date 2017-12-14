package org.schulcloud.mobile.data.local;

import android.util.Log;

import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.data.model.jsonApi.Included;
import org.schulcloud.mobile.data.model.jsonApi.IncludedAttributes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Provider;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import timber.log.Timber;


public class EventsDatabaseHelper extends BaseDatabaseHelper {

    @Inject
    EventsDatabaseHelper(Provider<Realm> realmProvider) {super(realmProvider);}

    public Observable<Event> setEvents(final Collection<org.schulcloud.mobile.data.model.Event> events) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed())
                return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(events));
            } catch (Exception e) {
                Timber.e(e, "There was an error while adding in Realm.");
                subscriber.onError(e);
            } finally {
                if (realm != null) {
                    realm.close();
                    subscriber.onCompleted();
                }
            }
        });
    }
    public Observable<List<Event>> getEvents() {
        final Realm realm = mRealmProvider.get();
        return realm.where(Event.class).findAllAsync().asObservable()
                .filter(RealmResults::isLoaded)
                .map(realm::copyFromRealm);
    }
    public Observable<List<Event>> getEventsForToday() {
        final Realm realm = mRealmProvider.get();
        return realm.where(Event.class).findAllAsync().asObservable()
                .map(events -> {
                    List<Event> e = realm.copyFromRealm(events);
                    int weekday = new GregorianCalendar().get(Calendar.DAY_OF_WEEK);
                    Log.d("Weekday", Integer.toString(weekday));

                    List<Event> eventsForWeekday = new ArrayList<>();
                    for (Event event : e)
                        if (event.included.size() > 0)
                            for (Included included : event.included) {
                                String freq = included.getAttributes().getFreq();
                                if (freq == null)
                                    continue;

                                String wkst = included.getAttributes().getWkst();
                                if (freq.equals(IncludedAttributes.FREQ_DAILY)
                                        || (freq.equals(IncludedAttributes.FREQ_WEEKLY)
                                        && getNumberForWeekday(wkst) == weekday)) {
                                    eventsForWeekday.add(event);
                                    break;
                                }
                            }

                    Calendar c = Calendar.getInstance();
                    Collections.sort(eventsForWeekday, (o1, o2) -> {
                        c.setTimeInMillis(Long.parseLong(o1.start));
                        Integer s1 = c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE);
                        c.setTimeInMillis(Long.parseLong(o2.start));
                        Integer s2 = c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE);
                        return s1.compareTo(s2);
                    });

                    return eventsForWeekday;
                }).debounce(100, TimeUnit.MILLISECONDS);
    }
    private int getNumberForWeekday(String weekday) {
        switch (weekday) {
            case "SU":
                return 1;
            case "MO":
                return 2;
            case "TU":
                return 3;
            case "WE":
                return 4;
            case "TH":
                return 5;
            case "FR":
                return 6;
            case "SA":
                return 7;
            default:
                return -1;
        }
    }
}
