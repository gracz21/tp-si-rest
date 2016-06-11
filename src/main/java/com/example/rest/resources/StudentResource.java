package com.example.rest.resources;

import com.example.rest.models.Grade;
import com.example.rest.utils.DatastoreHandlerUtil;
import com.example.rest.models.Course;
import com.example.rest.models.Student;
import com.example.rest.utils.GradesListUtil;
import org.mongodb.morphia.Datastore;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kamil Walkowiak
 */
@Path("/students")
public class StudentResource {
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Student> getStudents(@QueryParam("firstName") String firstName,
                                     @QueryParam("lastName") String lastName,
                                     @DefaultValue("0") @QueryParam("direction") int direction,
                                     @QueryParam("indexQuery") Long index,
                                     @QueryParam("dateOfBirthQuery") Date date, @QueryParam("firstNameQuery") String firstNameQuery,
                                     @QueryParam("lastNameQuery") String lastNameQuery) {
        Datastore datastore = DatastoreHandlerUtil.getInstance().getDatastore();

        List<Student> students = datastore.find(Student.class).asList();
        if(index != null) {
            students = students.stream().filter(student -> student.getIndex() == index).collect(Collectors.toList());
        }
        if(firstName != null) {
            students = students.stream().filter(student -> student.getFirstName().equals(firstName)).
                    collect(Collectors.toList());
        }
        if(lastName != null) {
            students = students.stream().filter(student -> student.getLastName().equals(lastName)).
                    collect(Collectors.toList());
        }

        if(date != null) {
            switch(direction) {
                case -1:
                    students = students.stream().filter(student -> student.getDateOfBirth().before(date))
                            .collect(Collectors.toList());
                    break;
                case 0:
                    students = students.stream().filter(student -> student.getDateOfBirth().equals(date))
                            .collect(Collectors.toList());
                    break;
                case 1:
                    students = students.stream().filter(student -> student.getDateOfBirth().after(date))
                            .collect(Collectors.toList());
                    break;
                default:
                    break;
            }
        }

        if(firstNameQuery != null && firstNameQuery.length() > 0) {
            students = students.stream().filter(student -> student.getFirstName().toLowerCase()
                    .contains(firstNameQuery.toLowerCase())).collect(Collectors.toList());
        }

        if(lastNameQuery != null && lastNameQuery.length() > 0) {
            students = students.stream().filter(student -> student.getLastName().toLowerCase()
                    .contains(lastNameQuery.toLowerCase())).collect(Collectors.toList());
        }

        return students;
    }

    @Path("/{index}")
    @GET
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getStudent(@PathParam("index") final long index) {
        Datastore datastore = DatastoreHandlerUtil.getInstance().getDatastore();
        Student returnedStudent = datastore.find(Student.class).field("index").equal(index).get();

        if(returnedStudent == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not found").build());
        }

        return Response.ok(returnedStudent).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createStudent(@NotNull @Valid Student student, @Context UriInfo uriInfo) {
        Datastore datastore = DatastoreHandlerUtil.getInstance().getDatastore();

        datastore.ensureIndexes();
        student.initializeIndex();
        datastore.save(student);
        URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(student.getIndex())).build();

        return Response.created(uri).entity(student).build();
    }

    @Path("/{index}")
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateStudent(@PathParam("index") final long index, Student studentParams) {
        Datastore datastore = DatastoreHandlerUtil.getInstance().getDatastore();
        Student updatedStudent = datastore.find(Student.class).field("index").equal(index).get();

        if(updatedStudent == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not found").build());
        } else {
            studentParams.setIndex(index);
            studentParams.setId(updatedStudent.getId());
        }

        datastore.save(studentParams);
        return Response.ok(studentParams).build();
    }

    @Path("/{index}")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteStudent(@PathParam("index") final long index) {
        Datastore datastore = DatastoreHandlerUtil.getInstance().getDatastore();
        Student student = datastore.find(Student.class).field("index").equal(index).get();

        if(student == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not found").build());
        }

        List<Course> courses = datastore.find(Course.class).asList();
        for(Course course: courses) {
            course.getGrades().removeAll(course.getStudentGradesList(index));
            datastore.save(course);
        }
        datastore.delete(student);

        return Response.ok("Student with index " + index + " removed").build();
    }

    @Path("/{index}/grades")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Grade> getGrades(@PathParam("index") final long index,
                                 @QueryParam("noteQuery") Double note, @QueryParam("dateQuery") Date date) {
        Datastore datastore = DatastoreHandlerUtil.getInstance().getDatastore();
        List<Course> courses = datastore.find(Course.class).asList();

        List<Grade> grades = courses.stream().
                flatMap(course -> course.getStudentGradesList(index).stream()).collect(Collectors.toList());

        if(note != null) {
            grades = GradesListUtil.getGradesByNote(grades, note, 0);
        }

        if(date != null) {
            grades = GradesListUtil.getGradesByDate(grades, date);
        }

        return grades;
    }
}
