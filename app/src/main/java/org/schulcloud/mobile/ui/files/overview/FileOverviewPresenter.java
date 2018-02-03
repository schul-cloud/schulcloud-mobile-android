package org.schulcloud.mobile.ui.files.overview;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.datamanagers.CourseDataManager;
import org.schulcloud.mobile.data.datamanagers.FileDataManager;
import org.schulcloud.mobile.data.model.Course;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

@ConfigPersistent
public class FileOverviewPresenter extends BasePresenter<FileOverviewMvpView> {

    private final FileDataManager mFileDataManager;
    private final CourseDataManager mCourseDataManager;
    private Subscription mCoursesSubscription;
    private boolean mIsFirstLoad = true;

    @Inject
    public FileOverviewPresenter(FileDataManager fileDataManager, CourseDataManager courseDataManager) {
        mFileDataManager = fileDataManager;
        mCourseDataManager = courseDataManager;
        sendToView(v -> load());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtil.unsubscribe(mCoursesSubscription);
    }

    public void load() {
        // Open folder directly if it is already set (e.g. from a previous session)
        if (mIsFirstLoad
                && mFileDataManager.getCurrentStorageContext().split("/", 3).length >= 2) {
            mIsFirstLoad = false;
            getViewOrThrow().showDirectory();
            return;
        }

        mFileDataManager.setCurrentStorageContextToRoot();
        RxUtil.unsubscribe(mCoursesSubscription);
        mCoursesSubscription = mCourseDataManager.getCourses()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        courses -> sendToView(view -> view.showCourses(courses)),
                        throwable -> sendToView(FileOverviewMvpView::showCoursesError)
                );
    }

    public void showMyFiles() {
        mFileDataManager.setCurrentStorageContextToMy();
        getViewOrThrow().showDirectory();
    }
    public void showCourseDirectory(@NonNull String courseId) {
        mFileDataManager.setCurrentStorageContextToCourse(courseId);
        getViewOrThrow().showDirectory();
    }
}
