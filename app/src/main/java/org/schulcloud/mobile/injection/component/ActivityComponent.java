package org.schulcloud.mobile.injection.component;

import org.schulcloud.mobile.injection.module.ActivityModule;
import org.schulcloud.mobile.injection.scope.PerActivity;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.base.BaseFragment;
import org.schulcloud.mobile.ui.courses.CourseActivity;
import org.schulcloud.mobile.ui.courses.detailed.DetailedCourseFragment;
import org.schulcloud.mobile.ui.courses.detailed.TopicFragment;
import org.schulcloud.mobile.ui.dashboard.DashboardActivity;
import org.schulcloud.mobile.ui.feedback.FeedbackFragment;
import org.schulcloud.mobile.ui.homework.HomeworkActivity;
import org.schulcloud.mobile.ui.files.FileActivity;
import org.schulcloud.mobile.ui.homework.add.AddHomeworkFragment;
import org.schulcloud.mobile.ui.homework.detailed.DetailedHomeworkFragment;
import org.schulcloud.mobile.ui.main.MainActivity;
import org.schulcloud.mobile.ui.settings.SettingsActivity;
import org.schulcloud.mobile.ui.signin.SignInActivity;

import dagger.Subcomponent;

/**
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(BaseActivity mainActivity);

    void inject(BaseFragment baseFragment);

    void inject(SignInActivity signInActivity);

    void inject(FileActivity fileActivity);

    void inject(MainActivity mainActivity);

    void inject(SettingsActivity settingsActivity);

    void inject(HomeworkActivity homeworkActivity);

    void inject(DetailedHomeworkFragment detailedHomeworkFragment);

    void inject(AddHomeworkFragment addHomeworkFragment);

    void inject(CourseActivity courseActivity);

    void inject (DetailedCourseFragment detailedCourseFragment);

    void inject(TopicFragment topicFragment);

    void inject(DashboardActivity dashboardActivity);

    void inject(FeedbackFragment feedbackFragment);
}
