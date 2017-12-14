package org.schulcloud.mobile.ui.courses.detailed;

import org.schulcloud.mobile.data.model.Course;
import org.schulcloud.mobile.data.model.Topic;
import org.schulcloud.mobile.ui.base.MvpView;

import java.util.List;

public interface DetailedCourseMvpView extends MvpView {

    void showCourse(Course course);

    void showTopics(List<Topic> topics);

    void showTopicDetail(String topicId, String topicName);

    void showError();
}
