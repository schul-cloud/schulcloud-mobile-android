package org.schulcloud.mobile.ui.dashboard;

import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.ui.base.MvpView;
import org.schulcloud.mobile.util.Pair;

import java.util.List;

public interface DashboardMvpView extends MvpView {

    void showOpenHomework(Pair<String, String> openHomework);

    void showEvents(List<Event> eventsForDay);

    void showCourseDetails(String xScCourseId);

}
