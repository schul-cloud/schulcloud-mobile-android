package org.schulcloud.mobile.data.model;

import org.parceler.Transient;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class File implements RealmModel {
    @PrimaryKey
    public String key;
    public String name;
    public String path;
    public String size;
    public String type;
    public String thumbnail;
    public String flatFileName;

    @Transient
    public String fullPath;

    // public Date lastModified;
}
