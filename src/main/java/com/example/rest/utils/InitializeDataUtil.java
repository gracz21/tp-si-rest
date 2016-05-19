package com.example.rest.utils;

import com.example.rest.models.Counter;
import com.example.rest.models.Course;
import com.example.rest.models.Grade;
import com.example.rest.models.Student;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import org.mongodb.morphia.Datastore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Kamil Walkowiak
 */
public class InitializeDataUtil {
    private static List<Student> students = new ArrayList<>();
    private static List<Course> courses = new ArrayList<>();
    private static List<Counter> counters = new ArrayList<>();

    public static void initializeData() throws ParseException {
        Datastore datastore = DatastoreHandlerUtil.getInstance().getDatastore();

        if(datastore.getCount(Counter.class) == 0) {
            initializeCounters(datastore);
        }

        if(datastore.getCount(Student.class) == 0) {
            initializeStudents(datastore);
        }

        if(datastore.getCount(Course.class) == 0) {
            initializeCourses(datastore);
            initializeGrades(datastore);
        }
    }

    private static void initializeCounters(Datastore datastore) {
        counters.add(new Counter("studentIndex"));
        counters.add(new Counter("courseId"));
        counters.add(new Counter("gradeId"));
        datastore.save(counters);
    }

    private static void initializeStudents(Datastore datastore) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        students.add(new Student("Kamil", "Walkowiak", formatter.parse("1993-03-24")));
        students.add(new Student("Test", "Student", formatter.parse("1990-01-01")));
        students.add(new Student("Other", "Student", formatter.parse("1994-09-13")));
        datastore.save(students);
    }

    private static void initializeCourses(Datastore datastore) {
        courses.add(new Course("TP-SI", "T. Pawlak"));
        courses.add(new Course("PIT", "A. Jaszkiewicz"));
        courses.add(new Course("MiASI", "B. Walter"));
        datastore.save(courses);
    }

    private static void initializeGrades(Datastore datastore) {
        datastore.update(courses.get(0), datastore.createUpdateOperations(Course.class).add("grades", new Grade(5.0, "2016-04-22", students.get(0), courses.get(0).getCourseId())));
        datastore.update(courses.get(0), datastore.createUpdateOperations(Course.class).add("grades", new Grade(5.0, "2016-04-29", students.get(0), courses.get(0).getCourseId())));
        datastore.update(courses.get(0), datastore.createUpdateOperations(Course.class).add("grades", new Grade(2.0, "2016-04-15", students.get(1), courses.get(0).getCourseId())));
        datastore.update(courses.get(1), datastore.createUpdateOperations(Course.class).add("grades", new Grade(5.0, "2016-04-20", students.get(0), courses.get(1).getCourseId())));
        datastore.update(courses.get(2), datastore.createUpdateOperations(Course.class).add("grades", new Grade(3.0, "2016-04-15", students.get(2), courses.get(2).getCourseId())));
    }
}
