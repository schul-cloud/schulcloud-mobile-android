package org.schulcloud.mobile.data.model;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class Device implements RealmModel {

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
