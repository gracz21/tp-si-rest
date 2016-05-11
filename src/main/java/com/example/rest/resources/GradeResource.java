package com.example.rest.resources;

import com.example.rest.utils.DatastoreHandlerUtil;
import com.example.rest.models.Course;
import com.example.rest.models.Grade;
import com.example.rest.models.Student;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kamil Walkowiak
 */
@Path("/students/{index}/courses/{courseId}/grades")
public class GradeResource {

    @GET
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Grade> getGrades(@PathParam("index") final long index, @PathParam("courseId") final long courseId,
                                 @DefaultValue("1") @QueryParam("direction") int direction, @QueryParam("note") Double note) {
        Datastore datastore = DatastoreHandlerUtil.getInstance().getDatastore();
        Course course = datastore.find(Course.class).field("courseId").equal(courseId).get();
        Student student = datastore.find(Student.class).field("index").equal(index).get();
        if(course == null || student == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not found").build());
        }

        List<Grade> grades = course.getStudentGradesList(index);
        if(note != null) {
            if(Grade.validateGivenNote(note)) {
                switch(direction) {
                    case -1:
                        grades = grades.stream().filter(grade -> grade.getNote() <= note).collect(Collectors.toList());
                        break;
                    case 1:
                        grades = grades.stream().filter(grade -> grade.getNote() >= note).collect(Collectors.toList());
                        break;
                    default:
                        break;
                }
            }
        }

        return grades;
    }

    @Path("/{id}")
    @GET
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getGrade(@PathParam("index") final long index, @PathParam("courseId") final long courseId,
                               @PathParam("id") final long id) {
        Datastore datastore = DatastoreHandlerUtil.getInstance().getDatastore();
        Course course = datastore.find(Course.class).field("courseId").equal(courseId).get();
        Student student = datastore.find(Student.class).field("index").equal(index).get();
        if(course == null || student == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not found").build());
        }

        Grade grade = course.getStudentGradesMape(index).get(id);
        if(grade == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not found").build());
        }
        return Response.ok(grade).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createGrade(@PathParam("index") final long index, @PathParam("courseId") final long courseId,
                                @NotNull @Valid Grade grade, @Context UriInfo uriInfo) {
        if(!grade.validateNote()) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).
                    entity("Note is not valid").build());
        }
        Datastore datastore = DatastoreHandlerUtil.getInstance().getDatastore();
        Course course = datastore.find(Course.class).field("courseId").equal(courseId).get();
        Student student = datastore.find(Student.class).field("index").equal(index).get();
        if(course == null || student == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not found").build());
        }

        grade.initId();
        grade.setStudent(student);
        grade.setCourseId(courseId);
        course.getGrades().add(grade);
        datastore.save(course);

        URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(grade.getId())).build();
        return Response.created(uri).entity(grade).build();
    }

    @Path("/{id}")
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateGrade(@PathParam("index") final long index, @PathParam("courseId") final long courseId,
                             @PathParam("id") final long id, @NotNull @Valid Grade gradeParams) {
        if(!gradeParams.validateNote()) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).
                    entity("Note is not valid").build());
        }
        Datastore datastore = DatastoreHandlerUtil.getInstance().getDatastore();
        Course course = datastore.find(Course.class).field("courseId").equal(courseId).get();
        Student student = datastore.find(Student.class).field("index").equal(index).get();
        if(course == null || student == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not found").build());
        }

        Grade gradeToUpdate = course.getStudentGradesMape(index).get(id);
        if(gradeToUpdate == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not found").build());
        } else {
            gradeParams.setId(id);
            gradeParams.setStudent(gradeToUpdate.getStudent());
            gradeParams.setCourseId(courseId);
        }
        Collections.replaceAll(course.getGrades(), gradeToUpdate, gradeParams);
        datastore.save(course);

        return Response.ok(gradeParams).build();
    }

    @Path("/{id}")
    @DELETE
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response deleteGrade(@PathParam("index") final long index, @PathParam("courseId") final long courseId,
                               @PathParam("id") final long id) {
        Datastore datastore = DatastoreHandlerUtil.getInstance().getDatastore();
        Course course = datastore.find(Course.class).field("courseId").equal(courseId).get();
        Student student = datastore.find(Student.class).field("index").equal(index).get();
        if(course == null || student == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not found").build());
        }

        Grade grade = course.getStudentGradesMape(index).get(id);
        if(grade == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not found").build());
        }
        course.getGrades().remove(grade);
        datastore.save(course);

        return Response.ok("Grade with index " + id + " removed").build();
    }
}
