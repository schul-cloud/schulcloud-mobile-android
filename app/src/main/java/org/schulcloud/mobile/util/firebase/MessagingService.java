package org.schulcloud.mobile.util.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.SchulCloudApplication;
import org.schulcloud.mobile.data.datamanagers.NotificationDataManager;
import org.schulcloud.mobile.data.model.requestBodies.CallbackRequest;
import org.schulcloud.mobile.ui.dashboard.DashboardFragment;

import javax.inject.Inject;

import rx.Subscription;

public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";

    private static final String KEY_TITLE = "title";
    private static final String KEY_BODY = "body";
    private static final String KEY_NEWS = "news";
    private static final String KEY_NOTIFICATION_ID = "notificationId";

    @Inject
    NotificationDataManager mNotificationDataManager;
    private Subscription mSubscription;

    @Override
    public void onCreate() {
        super.onCreate();
        SchulCloudApplication.get(this).getComponent().inject(this);
    }

    /**
     * Handles the FCM Message here
     *
     * @param remoteMessage push messaged received through firebase
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        JsonParser parser = new JsonParser();
        JsonObject message = parser.parse(remoteMessage.getData().get(KEY_NEWS))
                .getAsJsonObject();
        sendNotification(message.get(KEY_TITLE).getAsString(),
                message.get(KEY_BODY).getAsString());
        String notificationId = parser.parse(
                remoteMessage.getData().get(KEY_NOTIFICATION_ID)).getAsString();
        sendCallback(notificationId);

        // TODO: Implement Callback.
    }


    /**
     * Method to send a callback for a received message.
     *
     * @param notificationId id which identifies the notification.
     */
    private void sendCallback(String notificationId) {
        CallbackRequest callbackRequest = new CallbackRequest(notificationId, CallbackRequest.TYPE_RECEIVED);

        if (mSubscription != null && !mSubscription.isUnsubscribed())
            mSubscription.unsubscribe();
        mSubscription = mNotificationDataManager.sendCallback(callbackRequest)
                .subscribe();
    }

    /**
     * Method to send a notification to the application
     *
     * @param messageTitle is the title as it is set in the push messaged received through firebase
     * @param messageBody  is the body as it is set in the push messaged received through firebase
     */
    private void sendNotification(String messageTitle, String messageBody) {
        Intent intent = new Intent(this, DashboardFragment.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Improve Android Wear
        NotificationCompat.WearableExtender wearableExtender =
                new NotificationCompat.WearableExtender()
                .setHintHideIcon(true)
                .setContentIcon(R.mipmap.ic_launcher);
        notificationBuilder.extend(wearableExtender);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
