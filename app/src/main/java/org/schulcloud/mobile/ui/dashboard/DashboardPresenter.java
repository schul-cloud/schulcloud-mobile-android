package org.schulcloud.mobile.ui.dashboard;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

@ConfigPersistent
public class DashboardPresenter extends BasePresenter<DashboardMvpView> {

    @Inject
    public DashboardPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mSubscription);
    }

    public void showHomework() {
        getMvpView().showOpenHomework(mDataManager.getOpenHomeworks());
    }

    public void showEvents() {
        checkViewAttached();
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mDataManager.getEventsForToday()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(events -> getMvpView().showEvents(events));
    }

    public void showCourseDetails(String courseId) {
        getMvpView().showCourseDetails(courseId);
    }
}
