package com.example.rest.resources;

import com.example.rest.utils.DatastoreHandlerUtil;
import com.example.rest.models.Course;
import com.example.rest.models.Student;
import org.mongodb.morphia.Datastore;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;

/**
 * @author Kamil Walkowiak
 */
@Path("/students")
public class StudentResource {
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Student> getStudents() {
        Datastore datastore = DatastoreHandlerUtil.getInstance().getDatastore();
        return datastore.find(Student.class).asList();
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

        datastore.save(updatedStudent);
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
}