package org.schulcloud.mobile.ui.feedback;

import android.util.Log;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.requestBodies.FeedbackRequest;
import org.schulcloud.mobile.data.model.responseBodies.FeedbackResponse;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

@ConfigPersistent
public class FeedbackPresenter extends BasePresenter<FeedbackMvpView> {

    private Subscription feedbackSubscription;

    @Inject
    public FeedbackPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(FeedbackMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (feedbackSubscription != null) feedbackSubscription.unsubscribe();
    }


    public void sendFeedback(String format, String email, String content,
                             String contextName, String currentUser,
                             String subject, String to) {
        if (content.isEmpty()) {
            getMvpView().showContentHint();
        } else {
            String text = String.format(format, currentUser, email, contextName, content);

            FeedbackRequest feedbackRequest = new FeedbackRequest(text, subject, to);

            checkViewAttached();
            RxUtil.unsubscribe(feedbackSubscription);
            feedbackSubscription = mDataManager.sendFeedback(feedbackRequest)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<FeedbackResponse>() {
                        @Override
                        public void onCompleted() {
                            getMvpView().showFeedbackSent();
                            Log.e("Feedback", "onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("Feedback", "onError", e);
                        }

                        @Override
                        public void onNext(FeedbackResponse device) {
                        }
                    });
        }
    }
}
