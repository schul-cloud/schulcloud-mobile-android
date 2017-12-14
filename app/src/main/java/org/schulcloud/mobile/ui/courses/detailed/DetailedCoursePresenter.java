package org.schulcloud.mobile.ui.courses.detailed;

import org.schulcloud.mobile.data.datamanagers.CourseDataManager;
import org.schulcloud.mobile.data.datamanagers.TopicDataManager;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@ConfigPersistent
public class DetailedCoursePresenter extends BasePresenter<DetailedCourseMvpView> {

    @Inject
    CourseDataManager mCourseDataManager;
    @Inject
    TopicDataManager mTopicDataManager;

    @Inject
    public DetailedCoursePresenter(CourseDataManager courseDataManager,
                                   TopicDataManager topicDataManager) {
        mCourseDataManager = courseDataManager;
        mTopicDataManager = topicDataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mSubscription);
    }

    /**
     * Loads a specific course for a given id.
     *
     * @param courseId The ID of the course to load.
     */
    public void loadCourse(String courseId) {
        checkViewAttached();
        getMvpView().showCourse(mCourseDataManager.getCourseForId(courseId));
    }

    public void loadTopics() {
        checkViewAttached();
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mTopicDataManager.getTopics()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        topics -> getMvpView().showTopics(topics),
                        // onError
                        error -> {
                            Timber.e(error, "There was an error loading the topics.");
                            getMvpView().showError();
                        });
    }

    public void showTopicDetail(String topicId, String topicName) {
        getMvpView().showTopicDetail(topicId, topicName);
    }
}
