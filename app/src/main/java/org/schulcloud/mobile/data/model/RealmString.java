package org.schulcloud.mobile.data.model;

import io.realm.RealmModel;
import io.realm.annotations.RealmClass;

@RealmClass
public class RealmString implements RealmModel {
    public String value;

    public RealmString() {
    }
    public RealmString(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
