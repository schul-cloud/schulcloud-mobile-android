package org.schulcloud.mobile.ui.courses;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@ConfigPersistent
public class CoursePresenter extends BasePresenter<CourseMvpView> {

    @Inject
    public CoursePresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mSubscription);
    }

    public void loadCourses() {
        RxUtil.unsubscribe(mSubscription);
        if (!isViewAttached())
            return;

        mSubscription = mDataManager.getCourses()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        courses -> {
                            if (courses.isEmpty())
                                getMvpView().showCoursesEmpty();
                            else
                                getMvpView().showCourses(courses);
                        },
                        error -> {
                            Timber.e(error, "There was an error loading the courses.");
                            getMvpView().showError();
                        });
    }

    public void showCourseDetail(String courseId) {
        getMvpView().showCourseDetail(courseId);
    }
}
