package org.schulcloud.mobile.ui.courses;

import org.schulcloud.mobile.data.model.Course;
import org.schulcloud.mobile.ui.base.MvpView;

import java.util.List;

public interface CourseMvpView extends MvpView {

    void showCourses(List<Course> courses);

    void showCoursesEmpty();

    void showCourseDetail(String courseId);

    void showError();

}
