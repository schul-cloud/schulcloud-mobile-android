package org.schulcloud.mobile.data.model.jsonApi;


import org.parceler.Parcel;

import io.realm.IncludedAttributesRealmProxy;
import io.realm.RealmObject;

@Parcel(implementations = { IncludedAttributesRealmProxy.class },
        value = Parcel.Serialization.BEAN,
        analyze = { IncludedAttributes.class })
public class IncludedAttributes extends RealmObject {
    private String freq;
    private String until;
    private String wkst;

    public String getFreq() {
        return freq;
    }

    public void setFreq(String freq) {
        this.freq = freq;
    }

    public String getUntil() {
        return until;
    }

    public void setUntil(String until) {
        this.until = until;
    }

    public String getWkst() {
        return wkst;
    }

    public void setWkst(String wkst) {
        this.wkst = wkst;
    }
}
