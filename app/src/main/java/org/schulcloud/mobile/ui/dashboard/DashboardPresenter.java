package org.schulcloud.mobile.ui.dashboard;

import android.content.Context;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;

import java.util.List;

import javax.inject.Inject;

@ConfigPersistent
public class DashboardPresenter extends BasePresenter<DashboardMvpView> {

    @Inject
    public DashboardPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(DashboardMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void checkSignedIn(Context context) {
        super.isAlreadySignedIn(mDataManager, context);
    }

    public void showHomeworks() {
        getMvpView().showOpenHomeworks(mDataManager.getOpenHomeworks());
    }

    public void showEvents() {
        List<Event> events = mDataManager.getEventsForDay();
        getMvpView().showEvents(events);
    }

    public void showCourse(String courseId) {
        getMvpView().showCourse(courseId);
    }
}
