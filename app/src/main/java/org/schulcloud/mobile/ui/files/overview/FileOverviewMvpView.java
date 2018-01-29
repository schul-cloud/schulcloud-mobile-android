package org.schulcloud.mobile.ui.files.overview;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.model.Course;
import org.schulcloud.mobile.ui.base.MvpView;

import java.util.List;

public interface FileOverviewMvpView extends MvpView {

    void showCourses(@NonNull List<Course> courses);

    void showCoursesError();

    void showDirectory();

}
