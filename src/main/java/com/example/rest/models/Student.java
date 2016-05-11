package com.example.rest.models;

import com.example.rest.resources.StudentResource;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.mongodb.morphia.annotations.*;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Kamil Walkowiak
 */
@Entity("students")
@XmlRootElement
public class Student {
    private static final AtomicLong idCounter = new AtomicLong();

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
    private String dateOfBirth;

    @InjectLink(resource = StudentResource.class, method = "getStudent", style = InjectLink.Style.ABSOLUTE,
            bindings = @Binding(name = "index", value = "${instance.index}"), rel = "self")
    @XmlElement(name="link")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    private Link link;

    public Student() {
    }

    public Student(long index, String firstName, String lastName, String dateOfBirth) {
        this.index = index;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
    }

    public void initializeIndex() {
        this.index = idCounter.incrementAndGet();
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

    public String getDateOfBirth() {
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

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
