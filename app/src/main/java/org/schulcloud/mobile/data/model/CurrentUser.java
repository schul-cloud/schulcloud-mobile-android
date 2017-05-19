package org.schulcloud.mobile.data.model;


import org.parceler.Parcel;

import io.realm.CurrentUserRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


@Parcel(implementations = { CurrentUserRealmProxy.class },
        value = Parcel.Serialization.BEAN,
        analyze = { CurrentUser.class })
public class CurrentUser extends RealmObject {
    @PrimaryKey
    public String _id;
    public String firstName;
    public String lastName;
    public String email;
    public String schoolId;
    public String displayName;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}

