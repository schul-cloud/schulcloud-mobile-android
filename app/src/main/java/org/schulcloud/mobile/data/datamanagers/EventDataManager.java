package org.schulcloud.mobile.data.datamanagers;

import org.schulcloud.mobile.data.local.DatabaseHelper;
import org.schulcloud.mobile.data.local.EventsDatabaseHelper;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.data.remote.RestService;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func1;

@Singleton
public class EventDataManager {
    private final RestService mRestService;
    private final EventsDatabaseHelper mDatabaseHelper;

    @Inject
    PreferencesHelper mPreferencesHelper;
    @Inject
    UserDataManager userDataManager;

    @Inject
    public EventDataManager(RestService restService, PreferencesHelper preferencesHelper,
                                   EventsDatabaseHelper databaseHelper) {
        mRestService = restService;
        mPreferencesHelper = preferencesHelper;
        mDatabaseHelper = databaseHelper;
    }

    public PreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

    public Observable<Event> syncEvents() {
        return mRestService.getEvents(userDataManager.getAccessToken())
                .concatMap(new Func1<List<Event>, Observable<Event>>() {
                    @Override
                    public Observable<Event> call(List<Event> events) {
                        // clear old events
                        mDatabaseHelper.clearTable(Event.class);
                        return mDatabaseHelper.setEvents(events);
                    }
                }).doOnError(Throwable::printStackTrace);
    }

    public Observable<List<Event>> getEvents() {
        return mDatabaseHelper.getEvents().distinct();
    }

    public Observable<List<Event>> getEventsForToday() {
        return mDatabaseHelper.getEventsForToday();
    }
}
