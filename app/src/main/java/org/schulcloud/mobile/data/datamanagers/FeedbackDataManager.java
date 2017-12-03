package org.schulcloud.mobile.data.datamanagers;

import org.schulcloud.mobile.data.local.DatabaseHelper;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.local.SubmissionDatabaseHelper;
import org.schulcloud.mobile.data.model.requestBodies.FeedbackRequest;
import org.schulcloud.mobile.data.model.responseBodies.FeedbackResponse;
import org.schulcloud.mobile.data.remote.RestService;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func1;

@Singleton
public class FeedbackDataManager {
    private final RestService mRestService;
    private final DatabaseHelper mDatabaseHelper;

    @Inject
    PreferencesHelper mPreferencesHelper;
    @Inject
    UserDataManager userDataManager;

    @Inject
    public FeedbackDataManager(RestService restService, PreferencesHelper preferencesHelper,
                             DatabaseHelper databaseHelper) {
        mRestService = restService;
        mPreferencesHelper = preferencesHelper;
        mDatabaseHelper = databaseHelper;
    }

    public PreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

    public Observable<FeedbackResponse> sendFeedback(FeedbackRequest feedbackRequest) {
        return mRestService.sendFeedback(
                userDataManager.getAccessToken(),
                feedbackRequest)
                .concatMap(new Func1<FeedbackResponse, Observable<FeedbackResponse>>() {
                    @Override
                    public Observable<FeedbackResponse> call(FeedbackResponse feedbackResponse) {
                        return Observable.just(feedbackResponse);
                    }
                });
    }
}
