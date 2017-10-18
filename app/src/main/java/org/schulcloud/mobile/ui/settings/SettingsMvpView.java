package org.schulcloud.mobile.ui.settings;

import android.net.Uri;

import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.ui.base.MvpView;

import java.util.List;

public interface SettingsMvpView extends MvpView {
    void showEventsEmpty();

    void showDevices(List<Device> devices);

    void reload();

    void connectToCalendar(List<Event> events, Boolean promptForCalendar);

    void showSyncToCalendarSuccessful();

    void showContributors(String[] contributors);

    void showExternalContent(Uri uri);
}
