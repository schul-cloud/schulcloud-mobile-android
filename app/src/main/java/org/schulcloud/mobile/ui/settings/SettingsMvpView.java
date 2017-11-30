package org.schulcloud.mobile.ui.settings;

import android.net.Uri;
import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.ui.base.MvpView;

import java.util.List;

public interface SettingsMvpView extends MvpView {

    // Calender
    void showEventsEmpty();

    void connectToCalendar(@NonNull List<Event> events, boolean promptForCalendar);

    void showSyncToCalendarSuccessful();

    // Notifications
    void showDevices(@NonNull List<Device> devices);

    void reloadDevices();

    // About
    void showContributors(@NonNull String[] contributors);

    void showExternalContent(@NonNull Uri uri);

}
