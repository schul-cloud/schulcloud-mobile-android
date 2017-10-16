package org.schulcloud.mobile.data.model;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by araknor on 10.10.17.
 */

@RealmClass
public class News implements RealmModel {
    @PrimaryKey
    public String _id;
    public String schoolId;
    public String title;
    public String content;
    public String createdAt;
}
