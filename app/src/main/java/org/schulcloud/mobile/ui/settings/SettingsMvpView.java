package org.schulcloud.mobile.ui.settings;

import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.ui.base.MvpView;

import java.util.List;

public interface SettingsMvpView extends MvpView {
    void showEventsEmpty();

    void showDevices(List<Device> devices);

    void reload();

    void connectToCalendar(List<Event> events);

    void showSyncToCalendarSuccessful();
}
