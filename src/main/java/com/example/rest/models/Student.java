package com.example.rest.models;

import com.example.rest.resources.StudentResource;
import com.example.rest.utils.DatastoreHandlerUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.annotations.*;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Kamil Walkowiak
 */
@Entity("students")
@XmlRootElement
public class Student {
    @XmlTransient
    @Id
    private ObjectId id;

    @Indexed(name = "index", unique = true)
    private long index;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    @JsonFormat(shape=JsonFormat.Shape.STRING,
            pattern="yyyy-MM-dd", timezone="CET")
    private Date dateOfBirth;

    @InjectLink(resource = StudentResource.class, method = "getStudent", style = InjectLink.Style.ABSOLUTE,
            bindings = @Binding(name = "index", value = "${instance.index}"), rel = "self")
    @XmlElement(name="link")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    private Link link;

    public Student() {
    }

    public Student(String firstName, String lastName, Date dateOfBirth) {
        initializeIndex();
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
    }

    public void initializeIndex() {
        Datastore datastore = DatastoreHandlerUtil.getInstance().getDatastore();
        Query<Counter> query = datastore.find(Counter.class, "_id", "studentIndex");
        UpdateOperations<Counter> operation = datastore.createUpdateOperations(Counter.class).inc("seq");
        this.index = datastore.findAndModify(query, operation).getSeq();
    }

    @XmlTransient
    public ObjectId getId() {
        return id;
    }

    public long getIndex() {
        return index;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
