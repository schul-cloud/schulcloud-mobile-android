package org.schulcloud.mobile.data.model.jsonApi;

import io.realm.RealmModel;
import io.realm.annotations.RealmClass;

@RealmClass
public class Included implements RealmModel {
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
