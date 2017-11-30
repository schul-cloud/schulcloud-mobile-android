package org.schulcloud.mobile.ui.homework.add;

import org.schulcloud.mobile.ui.base.MvpView;

import java.util.List;

public interface AddHomeworkMvpView extends MvpView {

    void setCourses(List<String> courses);

    void setCanCreatePublic(boolean canCreatePublic);

    void showHomeworkSaved();

    void reloadHomeworkList();

    void showCourseLoadingError();

    void showSaveError();

    void showNameEmpty();

    void showInvalidDates();

}
