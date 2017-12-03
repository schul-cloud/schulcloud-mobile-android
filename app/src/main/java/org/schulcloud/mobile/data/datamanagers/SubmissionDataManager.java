package org.schulcloud.mobile.data.datamanagers;

import org.schulcloud.mobile.data.local.DatabaseHelper;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.local.SubmissionDatabaseHelper;
import org.schulcloud.mobile.data.model.Submission;
import org.schulcloud.mobile.data.remote.RestService;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func1;

@Singleton
public class SubmissionDataManager {
    private final RestService mRestService;
    private final SubmissionDatabaseHelper mDatabaseHelper;

    @Inject
    PreferencesHelper mPreferencesHelper;
    @Inject
    UserDataManager userDataManager;

    @Inject
    public SubmissionDataManager(RestService restService, PreferencesHelper preferencesHelper,
                                 SubmissionDatabaseHelper databaseHelper) {
        mRestService = restService;
        mPreferencesHelper = preferencesHelper;
        mDatabaseHelper = databaseHelper;
    }

    public PreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

    public Observable<Submission> syncSubmissions() {
        return mRestService.getSubmissions(userDataManager.getAccessToken())
                .concatMap(new Func1<List<Submission>, Observable<Submission>>() {
                    @Override
                    public Observable<Submission> call(List<Submission> submissions) {
                        // clear old devices
                        mDatabaseHelper.clearTable(Submission.class);
                        return mDatabaseHelper.setSubmissions(submissions);
                    }
                }).doOnError(Throwable::printStackTrace);
    }

    public Observable<List<Submission>> getSubmissions() {
        return mDatabaseHelper.getSubmissions().distinct();
    }

    public Submission getSubmissionForId(String homeworkId) {
        return mDatabaseHelper.getSubmissionForId(homeworkId);
    }
}
