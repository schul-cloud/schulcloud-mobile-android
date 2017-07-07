package org.schulcloud.mobile.data.model;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class CourseHomework implements RealmModel {
    @PrimaryKey
    public String _id;
    public String schoolId;
    public String name;
    public String description;
    public String color;
}
