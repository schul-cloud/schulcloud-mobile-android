package org.schulcloud.mobile.data.model;

import org.parceler.Parcel;

import io.realm.FileRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Parcel(implementations = { FileRealmProxy.class },
        value = Parcel.Serialization.BEAN,
        analyze = { File.class })
public class File extends RealmObject {
    @PrimaryKey
    public String key;
    public String name;
    public String path;
    public String size;
    public String type;
    public String thumbnail;

    // public Date lastModified;
}
