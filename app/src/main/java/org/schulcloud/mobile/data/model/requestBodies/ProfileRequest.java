package org.schulcloud.mobile.data.model.requestBodies;

public class ProfileRequest {
    public String displayName;
    public String password;
    public String _id;
    public String firstName;
    public String lastName;
    public String email;
    public String schoolId;
    public String gender;

    public ProfileRequest(String displayName, String password,String _id, String firstName,
                          String lastName, String email, String schoolId, String gender) {
        this._id = _id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
        this.displayName = displayName;
        this.password = password;
    }
}
