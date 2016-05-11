package com.example.rest.resources;

import com.example.rest.utils.DatastoreHandlerUtil;
import com.example.rest.models.Course;
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
import java.util.List;

/**
 * @author Kamil Walkowiak
 */
@Path("/courses")
public class CourseResource {
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Course> getCourses() {
        Datastore datastore = DatastoreHandlerUtil.getInstance().getDatastore();
        return datastore.find(Course.class).asList();
    }

    @Path("/{courseId}")
    @GET
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCourse(@PathParam("courseId") final long id) {
        Datastore datastore = DatastoreHandlerUtil.getInstance().getDatastore();
        Course returnedCourse = datastore.find(Course.class).field("courseId").equal(id).get();

        if(returnedCourse == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not found").build());
        }

        return Response.ok(returnedCourse).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createCourse(@NotNull @Valid Course course, @Context UriInfo uriInfo) {
        Datastore datastore = DatastoreHandlerUtil.getInstance().getDatastore();

        course.initializeCourseId();
        datastore.save(course);
        URI uri = uriInfo.getAbsolutePathBuilder().path(course.getId().toString()).build();
        return Response.created(uri).entity(course).build();
    }

    @Path("/{id}")
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateCourse(@PathParam("id") final long id, @NotNull @Valid Course courseParams) {
        Datastore datastore = DatastoreHandlerUtil.getInstance().getDatastore();
        Course courseToUpdate = datastore.find(Course.class).field("courseId").equal(id).get();

        if(courseToUpdate == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not found").build());
        } else {
            courseParams.setId(courseToUpdate.getId());
            courseParams.setCourseId(courseToUpdate.getCourseId());
        }
        datastore.save(courseParams);
        return Response.ok(courseParams).build();
    }

    @Path("/{id}")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteCourse(@PathParam("id") final long id) {
        Datastore datastore = DatastoreHandlerUtil.getInstance().getDatastore();
        Course course = datastore.find(Course.class).field("courseId").equal(id).get();

        if(course == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not found").build());
        }

        datastore.delete(course);
        return Response.ok("Course with index " + id + " removed").build();
    }
}