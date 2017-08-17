package org.schulcloud.mobile.data.model;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class CurrentUser implements RealmModel {
    public static final String PERMISSION_LESSONS_VIEW = "LESSONS_VIEW";
    public static final String PERMISSION_TOOL_NEW_VIEW = "TOOL_NEW_VIEW";
    public static final String PERMISSION_COURSE_EDIT = "COURSE_EDIT";
    public static final String PERMISSION_TEACHER_CREATE = "TEACHER_CREATE";
    public static final String PERMISSION_STUDENT_CREATE = "STUDENT_CREATE";
    public static final String PERMISSION_BASE_VIEW = "BASE_VIEW";
    public static final String PERMISSION_DASHBOARD_VIEW = "DASHBOARD_VIEW";
    public static final String PERMISSION_TOOL_VIEW = "TOOL_VIEW";

    @PrimaryKey
    public String _id;
    public String firstName;
    public String lastName;
    public String email;
    public String schoolId;
    public String displayName;

    public RealmList<RealmString> permissions;

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

    public RealmList<RealmString> getPermissions() {
        return permissions;
    }
    public boolean hasPermission(String permission) {
        if (permission == null || permission.isEmpty())
            return false;
        for (RealmString p : permissions)
            if (permission.equalsIgnoreCase(p.value))
                return true;
        return false;
    }
    public void setPermissions(RealmList<RealmString> permissions) {
        this.permissions = permissions;
    }
}

