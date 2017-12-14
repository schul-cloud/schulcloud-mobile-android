package org.schulcloud.mobile.data.model.responseBodies;

import com.google.gson.annotations.SerializedName;

import org.schulcloud.mobile.data.model.Course;
import org.schulcloud.mobile.data.model.CourseHomework;
import org.schulcloud.mobile.data.model.Homework;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class AddHomeworkResponse implements RealmModel {
    @PrimaryKey
    public String _id;
    public String schoolId;
    public String teacherId;
    public String name;
    public String courseId;
    public String description;
    public String availableDate;
    public String dueDate;

    @SerializedName("private")
    public Boolean restricted;

    public Homework toHomework(Course course) {
        Homework homework = new Homework();
        homework._id = _id;
        homework.schoolId = schoolId;
        homework.teacherId = teacherId;
        homework.name = name;
        homework.description = description;
        homework.availableDate = availableDate;
        homework.dueDate = dueDate;
        homework.restricted = restricted;

        if (course == null)
            homework.courseId = null;
        else {
            CourseHomework courseHomework = new CourseHomework();
            courseHomework._id = course._id;
            courseHomework.schoolId = course.schoolId;
            courseHomework.name = course.name;
            courseHomework.description = course.name;
            courseHomework.color = course.color;
            homework.courseId = courseHomework;
        }

        return homework;
    }
}
