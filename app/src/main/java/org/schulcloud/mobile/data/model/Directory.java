package org.schulcloud.mobile.data.model;

import org.parceler.Parcel;

import io.realm.DirectoryRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Parcel(implementations = { DirectoryRealmProxy.class },
        value = Parcel.Serialization.BEAN,
        analyze = { Directory.class })
public class Directory extends RealmObject {
    @PrimaryKey
    public String name;
}
