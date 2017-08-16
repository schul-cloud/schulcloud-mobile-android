package org.schulcloud.mobile.ui.settings;

import android.content.Context;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.data.model.jsonApi.Included;
import org.schulcloud.mobile.data.model.requestBodies.DeviceRequest;
import org.schulcloud.mobile.data.model.responseBodies.DeviceResponse;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.CalendarContentUtil;
import org.schulcloud.mobile.util.DaysBetweenUtil;
import org.schulcloud.mobile.util.RxUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Response;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@ConfigPersistent
public class SettingsPresenter extends BasePresenter<SettingsMvpView> {

    private Subscription eventSubscription;

    @Inject
    public SettingsPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(SettingsMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
        if (eventSubscription != null) eventSubscription.unsubscribe();
    }

    /**
     * fetching events from local db
     * @param promptForCalendar {Boolean} - whether to open a calendar-choose-dialog
     */
    public void loadEvents(Boolean promptForCalendar) {
        checkViewAttached();
        RxUtil.unsubscribe(eventSubscription);
        eventSubscription = mDataManager.getEvents()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Event>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "There was an error loading the events.");
                    }

                    @Override
                    public void onNext(List<Event> events) {
                        if (events.isEmpty()) {
                            getMvpView().showEventsEmpty();
                        } else {
                            getMvpView().connectToCalendar(events, promptForCalendar);
                        }
                    }
                });
    }

    /**
     * Syncs given Events to local calendar
     *
     * @param calendarName          {String} - the calendar in which the events will be inserted
     * @param events              {List<Event>} - the events which will be inserted into the calendar
     * @param calendarContentUtil {CalendarContentUtil} - an instance of the CalendarContentUtil for handling the local calendar storage
     * @param showToast             {Boolean} - whether to show a toast after writing
     */
    public void writeEventsToLocalCalendar(String calendarName, List<Event> events, CalendarContentUtil calendarContentUtil, Boolean showToast) {
        for (Event event : events) {
            // syncing by deleting first and writing again
            calendarContentUtil.deleteEventByUid(event._id);

            String recurringRule = null;

            // handle recurrent events
            try {
                Included includedInformation = event.included.get(0);
                if (includedInformation.getType().equals(CalendarContentUtil.RECURRENT_TYPE)) {
                    StringBuilder builder = new StringBuilder();

                    // count days/weeks from startDate to untilDate
                    Date startDate = new Date(Long.parseLong(event.start));
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date untilDate = dateFormat.parse(includedInformation.getAttributes().getUntil());
                    Integer betweenDates = 0;
                    switch (includedInformation.getAttributes().getFreq()) {
                        case "WEEKLY":
                            betweenDates = DaysBetweenUtil.weeksBetween(startDate, untilDate);
                            break;
                        case "DAILY":
                            betweenDates = DaysBetweenUtil.daysBetween(startDate, untilDate);
                            break;
                        default:
                            break;
                    }

                    builder
                            .append("FREQ=")
                            .append(includedInformation.getAttributes().getFreq())
                            .append(";")
                            .append("COUNT=")
                            .append(betweenDates)
                            .append(";")
                            .append("WKST=")
                            .append(includedInformation.getAttributes().getWkst());

                    recurringRule = builder.toString();
                }
            } catch (Exception e) {
                // do nothing when its not a recurrent event
            }

            calendarContentUtil.createEvent(calendarName, event, recurringRule);
        }

        if (showToast) getMvpView().showSyncToCalendarSuccessful();
    }


    public void checkSignedIn(Context context) {
        super.isAlreadySignedIn(mDataManager, context);
    }

    public String getFireBaseToken() {
        return FirebaseInstanceId.getInstance().getToken();
    }

    public void registerDevice() {

        if (mDataManager.getPreferencesHelper().getMessagingToken().equals("null")) {
            String token = getFireBaseToken();
            Log.d("FirebaseID", "Refreshed token: " + token);

            Log.d("FirebaseID", "sending registration to Server");
            DeviceRequest deviceRequest = new DeviceRequest("firebase", "mobile", android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")", mDataManager.getCurrentUserId(), token, android.os.Build.VERSION.INCREMENTAL);

            if (mSubscription != null && !mSubscription.isUnsubscribed())
                mSubscription.unsubscribe();
            mSubscription = mDataManager.createDevice(deviceRequest, token)
                    .subscribe(new Subscriber<DeviceResponse>() {
                        @Override
                        public void onCompleted() {
                            getMvpView().reload();
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onNext(DeviceResponse device) {
                        }
                    });
        }
    }

    public void loadDevices() {
        checkViewAttached();
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mDataManager.getDevices()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Device>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "There was an error loading the users.");
                        //TODO: Show error
                    }

                    @Override
                    public void onNext(List<Device> devices) {
                        if (devices.isEmpty()) {
                            // TODO: Show something
                        } else {
                            getMvpView().showDevices(devices);
                        }
                    }
                });
    }

    public void deleteDevice(Device device) {
        if (mSubscription != null && !mSubscription.isUnsubscribed())
            mSubscription.unsubscribe();
        checkViewAttached();
        mSubscription = mDataManager.deleteDevice(device.token)
                .subscribe(
                        new Subscriber<Response<Void>>() {
                            @Override
                            public void onCompleted() {
                                getMvpView().reload();
                                mDataManager.getPreferencesHelper().clear(PreferencesHelper.PREFERENCE_MESSAGING_TOKEN);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Response<Void> empty) {

                            }
                        });
    }

}
