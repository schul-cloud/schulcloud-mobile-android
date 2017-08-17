package org.schulcloud.mobile.data.model.requestBodies;

import com.google.gson.annotations.SerializedName;

public class AddHomeworkRequest {
    public String schoolId;
    public String teacherId;
    public String name;
    public String courseId;
    public String description;
    public String availableDate;
    public String dueDate;
    public Boolean publicSubmissions;

    @SerializedName("private")
    public Boolean restricted;

    public AddHomeworkRequest(String schoolId, String teacherId, String name,
                              String courseId, String description,
                              String availableDate, String dueDate,
                              Boolean publicSubmissions, Boolean restricted) {
        this.schoolId = schoolId;
        this.teacherId = teacherId;
        this.name = name;
        this.courseId = courseId;
        this.description = description;
        this.availableDate = availableDate;
        this.dueDate = dueDate;
        this.publicSubmissions = publicSubmissions;
        this.restricted = restricted;
    }
}
