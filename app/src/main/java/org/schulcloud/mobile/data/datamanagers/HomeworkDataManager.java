package org.schulcloud.mobile.data.datamanagers;

import org.schulcloud.mobile.data.local.HomeworkDatabaseHelper;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.requestBodies.AddHomeworkRequest;
import org.schulcloud.mobile.data.model.responseBodies.AddHomeworkResponse;
import org.schulcloud.mobile.data.remote.RestService;
import org.schulcloud.mobile.util.Pair;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func1;

@Singleton
public class HomeworkDataManager {
    private final RestService mRestService;
    private final HomeworkDatabaseHelper mDatabaseHelper;

    @Inject
    PreferencesHelper mPreferencesHelper;
    @Inject
    UserDataManager userDataManager;

    @Inject
    public HomeworkDataManager(RestService restService, PreferencesHelper preferencesHelper,
                            HomeworkDatabaseHelper databaseHelper) {
        mRestService = restService;
        mPreferencesHelper = preferencesHelper;
        mDatabaseHelper = databaseHelper;
    }

    public PreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

    public Observable<Homework> syncHomework() {
        return mRestService.getHomework(userDataManager.getAccessToken())
                .concatMap(new Func1<List<Homework>, Observable<Homework>>() {
                    @Override
                    public Observable<Homework> call(List<Homework> homeworks) {
                        // clear old devices
                        mDatabaseHelper.clearTable(Homework.class);
                        return mDatabaseHelper.setHomework(homeworks);
                    }
                }).doOnError(Throwable::printStackTrace);
    }

    public Observable<List<Homework>> getHomework() {
        return mDatabaseHelper.getHomework().distinct();
    }

    public Homework getHomeworkForId(String homeworkId) {
        return mDatabaseHelper.getHomeworkForId(homeworkId);
    }

    public Pair<String, String> getOpenHomeworks() {
        return mDatabaseHelper.getOpenHomeworks();
    }

    public Observable<AddHomeworkResponse> addHomework(AddHomeworkRequest addHomeworkRequest) {
        return mRestService.addHomework(userDataManager.getAccessToken(), addHomeworkRequest);
    }
}
