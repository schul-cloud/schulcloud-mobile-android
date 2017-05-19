package org.schulcloud.mobile.data.model.jsonApi;


import org.parceler.Parcel;

import io.realm.IncludedRealmProxy;
import io.realm.RealmObject;

@Parcel(implementations = { IncludedRealmProxy.class },
        value = Parcel.Serialization.BEAN,
        analyze = { Included.class })
public class Included extends RealmObject {
    private String type;
    private String id;
    private IncludedAttributes attributes;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public IncludedAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(IncludedAttributes attributes) {
        this.attributes = attributes;
    }
}
