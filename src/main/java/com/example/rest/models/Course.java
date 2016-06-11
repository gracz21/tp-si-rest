package com.example.rest.models;

import com.example.rest.resources.CourseResource;
import com.example.rest.utils.DatastoreHandlerUtil;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Kamil Walkowiak
 */
@Entity("courses")
@XmlRootElement
public class Course {
    @XmlTransient
    @Id
    private ObjectId id;

    @Indexed(name = "courseId", unique = true)
    private long courseId;

    @NotNull
    private String name;

    @NotNull
    private String leader;

    @Embedded
    private List<Grade> grades;

    @InjectLinks({
            @InjectLink(resource = CourseResource.class, method = "getCourse", style = InjectLink.Style.ABSOLUTE,
                    bindings = @Binding(name = "courseId", value = "${instance.courseId}"), rel = "self")
    })
    @XmlElement(name="link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    private List<Link> links;

    public Course() {
        this.grades = new LinkedList<>();
    }

    public Course(String name, String leader) {
        initializeCourseId();
        this.name = name;
        this.leader = leader;
        this.grades = new LinkedList<>();
    }

    public void initializeCourseId() {
        Datastore datastore = DatastoreHandlerUtil.getInstance().getDatastore();
        Query<Counter> query = datastore.find(Counter.class, "_id", "courseId");
        UpdateOperations<Counter> operation = datastore.createUpdateOperations(Counter.class).inc("seq");
        this.courseId = datastore.findAndModify(query, operation).getSeq();
    }

    @XmlTransient
    public ObjectId getId() {
        return id;
    }

    public long getCourseId() {
        return courseId;
    }

    public String getName() {
        return name;
    }

    public String getLeader() {
        return leader;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public List<Grade> getStudentGradesList(long index) {
        return grades.stream().filter(grade -> grade.getStudent().getIndex() == index).collect(Collectors.toList());
    }

    public Map<Long, Grade> getStudentGradesMape(long index) {
        return grades.stream().filter(grade -> grade.getStudent().getIndex() == index).
                collect(Collectors.toMap(Grade::getId, Function.identity()));
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }
}
