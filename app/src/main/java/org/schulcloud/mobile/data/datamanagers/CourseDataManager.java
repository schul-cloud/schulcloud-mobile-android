package org.schulcloud.mobile.data.datamanagers;

import org.schulcloud.mobile.data.local.CourseDatabaseHelper;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.model.Course;
import org.schulcloud.mobile.data.model.responseBodies.FeathersResponse;
import org.schulcloud.mobile.data.remote.RestService;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func1;

@Singleton
public class CourseDataManager {
    private final RestService mRestService;
    private final CourseDatabaseHelper mDatabaseHelper;

    @Inject
    PreferencesHelper mPreferencesHelper;
    @Inject
    UserDataManager userDataManager;

    @Inject
    public CourseDataManager(RestService restService, PreferencesHelper preferencesHelper,
                           CourseDatabaseHelper databaseHelper) {
        mRestService = restService;
        mPreferencesHelper = preferencesHelper;
        mDatabaseHelper = databaseHelper;
    }

    public PreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

    public Observable<Course> syncCourses() {
        return mRestService.getCourses(userDataManager.getAccessToken())
                .concatMap(new Func1<FeathersResponse<Course>, Observable<Course>>() {
                    @Override
                    public Observable<Course> call(FeathersResponse<Course> courses) {
                        mDatabaseHelper.clearTable(Course.class);
                        return mDatabaseHelper.setCourses(courses.data);
                    }
                })
                .doOnError(Throwable::printStackTrace);
    }

    public Observable<List<Course>> getCourses() {
        return mDatabaseHelper.getCourses().distinct();
    }

    public Course getCourseForId(String courseId) {
        return mDatabaseHelper.getCourseForId(courseId);
    }
}
