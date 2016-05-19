package com.example.rest.models;

import com.example.rest.resources.CourseResource;
import com.example.rest.resources.GradeResource;
import com.example.rest.resources.StudentResource;
import com.example.rest.utils.DatastoreHandlerUtil;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.annotations.*;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Kamil Walkowiak
 */
@XmlRootElement
@Embedded
public class Grade {
    private static final double[] noteScale = new double[]{2.0, 3.0, 3.5, 4.0, 4.5, 5.0};

    private long id;
    @NotNull
    private double note;

    @NotNull
    private String date;

    @Reference
    private Student student;
    private long courseId;

    @InjectLinks({
            @InjectLink(resource = GradeResource.class, method = "getGrade", style = InjectLink.Style.ABSOLUTE,
                    bindings = {
                            @Binding(name = "index", value = "${instance.student.index}"),
                            @Binding(name = "courseId", value = "${instance.courseId}"),
                            @Binding(name = "id", value = "${instance.id}")
                    }, rel = "self"),
            @InjectLink(resource = CourseResource.class, method = "getCourse", style = InjectLink.Style.ABSOLUTE,
                    bindings = @Binding(name = "courseId", value = "${instance.courseId}"), rel = "course"),
            @InjectLink(resource = StudentResource.class, method = "getStudent", style = InjectLink.Style.ABSOLUTE,
                    bindings = @Binding(name = "index", value = "${instance.student.index}"), rel = "student")
    })
    @XmlElement(name="link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    private List<Link> links;

    public Grade() {
    }

    public Grade(double note, String date, Student student, long courseId) {
        initId();
        this.note = note;
        this.date = date;
        this.student = student;
        this.courseId = courseId;
    }

    public void initId() {
        Datastore datastore = DatastoreHandlerUtil.getInstance().getDatastore();
        Query<Counter> query = datastore.find(Counter.class, "_id", "gradeId");
        UpdateOperations<Counter> operation = datastore.createUpdateOperations(Counter.class).inc("seq");
        this.id = datastore.findAndModify(query, operation).getSeq();
    }

    public long getId() {
        return id;
    }

    public double getNote() {
        return note;
    }

    public String getDate() {
        return date;
    }

    public Student getStudent() {
        return student;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setNote(double note) {
        this.note = note;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public boolean validateNote() {
        boolean result = false;

        for(double element: noteScale) {
            if(this.note == element) {
                result = true;
            }
        }

        return result;
    }

    public static boolean validateGivenNote(double note) {
        boolean result = false;

        for(double element: noteScale) {
            if(note == element) {
                result = true;
            }
        }

        return result;
    }
}
