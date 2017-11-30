package org.schulcloud.mobile.ui.homework.detailed;

import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.Submission;
import org.schulcloud.mobile.ui.base.MvpView;

public interface DetailedHomeworkMvpView extends MvpView {

    void showHomework(Homework homework);

    void showSubmission(Submission submission, String userId);

}
