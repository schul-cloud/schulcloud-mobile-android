package org.schulcloud.mobile.data.sync;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.schulcloud.mobile.SchulCloudApplication;
import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.News;
import org.schulcloud.mobile.util.AndroidComponentUtil;
import org.schulcloud.mobile.util.NetworkUtil;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by araknor on 10.10.17.
 */

public class NewsSyncService extends Service {
    @Inject
    DataManager mDataManager;
    private Subscription mSubscription;
    private String schoolId;
    private String ARGUMENT_SCHOOL_ID = "schoolId";

    public static Intent getStartIntent(Context context) {
        return new Intent(context, NewsSyncService.class);
    }

    public static boolean isRunning(Context context){
        return AndroidComponentUtil.isServiceRunning(context,NewsSyncService.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SchulCloudApplication.get(this).getComponent().inject(this);
    }

    @Override
    public int onStartCommand(Intent intent , int flags, final int startId) {
        Timber.i("starting news sync");
        if (!NetworkUtil.isNetworkConnected(this)) {
            Timber.i("Sync canceled, connection not available");
            AndroidComponentUtil.toggleComponent(this, CourseSyncService.SyncOnConnectionAvailable.class, true);
            stopSelf(startId);
            return START_NOT_STICKY;
        }

        Bundle extras = intent.getExtras();
        if(extras != null) {
            schoolId = extras.getString(ARGUMENT_SCHOOL_ID);
        }

        if (mSubscription != null && !mSubscription.isUnsubscribed()) mSubscription.unsubscribe();
        mSubscription = mDataManager.syncNews(schoolId)
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<News>() {
                    @Override
                    public void onCompleted() {
                        Timber.i("synced succesfull");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Failed to sync news");
                        stopSelf(startId);
                    }

                    @Override
                    public void onNext(News news) {
                    }
                });
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
