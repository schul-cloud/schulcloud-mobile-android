package org.schulcloud.mobile.data.model;

import org.parceler.Parcel;

import io.realm.DeviceRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Parcel(implementations = { DeviceRealmProxy.class },
        value = Parcel.Serialization.BEAN,
        analyze = { Device.class })
public class Device extends RealmObject {

    @PrimaryKey
    public String _id;
    public String token;
    public String type;
    public String service;
    public String name;
    public String OS;
    public String state;
    public String updatedAt;
    public String createdAt;
    public String active;

}
