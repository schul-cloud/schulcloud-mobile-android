package org.schulcloud.mobile.data.datamanagers;

import android.util.Log;

import org.schulcloud.mobile.data.local.NotificationsDatabaseHelper;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.data.model.requestBodies.CallbackRequest;
import org.schulcloud.mobile.data.model.requestBodies.DeviceRequest;
import org.schulcloud.mobile.data.model.responseBodies.DeviceResponse;
import org.schulcloud.mobile.data.remote.RestService;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Response;
import rx.Observable;
import rx.functions.Func1;

@Singleton
public class NotificationDataManager {

    private final RestService mRestService;
    private final NotificationsDatabaseHelper mDatabaseHelper;

    @Inject
    PreferencesHelper mPreferencesHelper;
    @Inject
    UserDataManager userDataManager;

    @Inject
    public NotificationDataManager(RestService restService, PreferencesHelper preferencesHelper,
                           NotificationsDatabaseHelper databaseHelper) {
        mRestService = restService;
        mPreferencesHelper = preferencesHelper;
        mDatabaseHelper = databaseHelper;
    }

    public PreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

    public Observable<DeviceResponse> createDevice(DeviceRequest deviceRequest, String token) {
        return mRestService.createDevice(
                userDataManager.getAccessToken(),
                deviceRequest)
                .concatMap(new Func1<DeviceResponse, Observable<DeviceResponse>>() {
                    @Override
                    public Observable<DeviceResponse> call(DeviceResponse deviceResponse) {
                        Log.i("[DEVICE]", deviceResponse.id);
                        mPreferencesHelper.saveMessagingToken(token);
                        return Observable.just(deviceResponse);
                    }
                });
    }

    public Observable<Device> syncDevices() {
        return mRestService.getDevices(userDataManager.getAccessToken())
                .concatMap(new Func1<List<Device>, Observable<Device>>() {
                    @Override
                    public Observable<Device> call(List<Device> devices) {
                        // clear old devices
                        mDatabaseHelper.clearTable(Device.class);
                        return mDatabaseHelper.setDevices(devices);
                    }
                }).doOnError(Throwable::printStackTrace);
    }

    public Observable<List<Device>> getDevices() {
        return mDatabaseHelper.getDevices().distinct();
    }

    public Observable<Response<Void>> sendCallback(CallbackRequest callbackRequest) {
        return mRestService.sendCallback(userDataManager.getAccessToken(), callbackRequest);
    }

    public Observable<Response<Void>> deleteDevice(String deviceId) {
        return mRestService.deleteDevice(userDataManager.getAccessToken(), deviceId);
    }
}
