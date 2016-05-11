package com.example.rest.utils;

import com.example.rest.models.Course;
import com.example.rest.models.Grade;
import com.example.rest.models.Student;
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

    public static void initializeData() throws ParseException {
        Datastore datastore = DatastoreHandlerUtil.getInstance().getDatastore();

        DB db = datastore.getDB();
        db.getCollection("students").drop();
        db.getCollection("courses").drop();

        initializeStudents(datastore);
        initializeCourses(datastore);
        initializeGrades(datastore);
    }

    private static void initializeStudents(Datastore datastore) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        students.add(new Student(109714, "Kamil", "Walkowiak", formatter.parse("1993-03-24")));
        students.add(new Student(343287, "Test", "Student", formatter.parse("1990-01-01")));
        students.add(new Student(111111, "Other", "Student", formatter.parse("1994-09-13")));
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
