package org.schulcloud.mobile.data.model;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class HomeworkTest {
    private static final String ID = "ID";
        private static final String SCHOOLDID = "schoolId";
        private static final String TEACHERID = "teacherId";
        private static final String NAME = "name";
        private static final String DESCRIPTION = "description";
        private static final String AVAILABLEDATE = "availabledate";
        private static final String DUEDATE = "dueDate";
        private static final CourseHomework COURSEID = new CourseHomework();
        private static final Boolean RESTRICTED = true;

        private Homework homework;

        @Before
        public void setUp() throws Exception {
            homework = createNewHomework();
        }

        @Test
        public void testGetProperties() {
            assertEquals(homework._id, ID);
            assertEquals(homework.schoolId, SCHOOLDID);
            assertEquals(homework.teacherId, TEACHERID);
            assertEquals(homework.name, NAME);
            assertEquals(homework.description, DESCRIPTION);
            assertEquals(homework.availableDate, AVAILABLEDATE);
            assertEquals(homework.dueDate, DUEDATE);
            assertEquals(homework.courseId, COURSEID);
            assertEquals(homework.restricted, RESTRICTED);
        }

    public static Homework createNewHomework() {
        Homework h = new Homework();
        h._id = ID;
        h.schoolId = SCHOOLDID;
        h.teacherId = TEACHERID;
        h.name = NAME;
        h.description = DESCRIPTION;
        h.availableDate = AVAILABLEDATE;
        h.dueDate = DUEDATE;
        h.courseId = COURSEID;
        h.restricted = RESTRICTED;

        return h;
    }
}