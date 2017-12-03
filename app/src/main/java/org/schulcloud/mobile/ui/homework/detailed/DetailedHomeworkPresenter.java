package org.schulcloud.mobile.ui.homework.detailed;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.datamanagers.HomeworkDataManager;
import org.schulcloud.mobile.data.datamanagers.SubmissionDataManager;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

@ConfigPersistent
public class DetailedHomeworkPresenter extends BasePresenter<DetailedHomeworkMvpView> {

    @Inject
    HomeworkDataManager mHomeworkDataManager;
    @Inject
    SubmissionDataManager mSubmissionDataManager;
    @Inject
    UserDataManager mUserDataManager;

    @Inject
    public DetailedHomeworkPresenter(HomeworkDataManager homeworkDataManager,
                                     SubmissionDataManager submissionDataManager,
                                     UserDataManager userDataManager) {
        mHomeworkDataManager = homeworkDataManager;
        mSubmissionDataManager = submissionDataManager;
        mUserDataManager = userDataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mSubscription);
    }

    /**
     * Loads a specific homework for a given id.
     *
     * @param homeworkId The id of the homework to be shown.
     */
    public void loadHomework(String homeworkId) {
        checkViewAttached();
        getMvpView().showHomework(mHomeworkDataManager.getHomeworkForId(homeworkId));
    }

    /**
     * Loads a specific submission containing the comments.
     *
     * @param homeworkId The id of the displayed homework. Required to reference the submission.
     */
    public void loadComments(String homeworkId) {
        checkViewAttached();
        RxUtil.unsubscribe(mSubscription);
        getMvpView().showSubmission(mSubmissionDataManager.getSubmissionForId(homeworkId),
                mUserDataManager.getCurrentUserId());
    }
}
