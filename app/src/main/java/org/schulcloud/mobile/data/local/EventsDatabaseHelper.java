package org.schulcloud.mobile.data.local;

import android.util.Log;

import org.schulcloud.mobile.data.model.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import io.realm.Realm;
import rx.Observable;
import timber.log.Timber;


public class EventsDatabaseHelper extends BaseDatabaseHelper {

    @Inject
    EventsDatabaseHelper(Provider<Realm> realmProvider) {super(realmProvider);}

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

    public List<Event> getEventsForDay() {
        final Realm realm = mRealmProvider.get();
        Collection<Event> events = realm.where(Event.class).findAll();

        int weekday = new GregorianCalendar().get(Calendar.DAY_OF_WEEK);
        Log.d("Weekday", Integer.toString(weekday));

        List<Event> eventsForWeekday = new ArrayList<>();

        for (Event event : events) {
            if (event.included.size() > 0) {
                if (getNumberForWeekday(event.included.first().getAttributes().getWkst()) == weekday) {
                    eventsForWeekday.add(event);
                }
            }
        }

        Collections.sort(eventsForWeekday, new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                return o1.start.compareTo(o2.start);
            }
        });

        return eventsForWeekday;
    }

    public int getNumberForWeekday(String weekday) {
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
