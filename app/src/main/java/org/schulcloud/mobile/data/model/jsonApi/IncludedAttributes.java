package org.schulcloud.mobile.data.model.jsonApi;


import io.realm.RealmModel;
import io.realm.annotations.RealmClass;

@RealmClass
public class IncludedAttributes implements RealmModel {
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
