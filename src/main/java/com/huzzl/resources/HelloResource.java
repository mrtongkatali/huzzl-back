package com.huzzl.resources;

import com.codahale.metrics.annotation.Timed;
import com.huzzl.resources.response.GenericResponse;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;

import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class HelloResource {

    public HelloResource() { }

    @GET
    @UnitOfWork
    @Timed
    public Response getResponse(@PathParam("name") String name) {

        Map<String, Object> data = new HashMap();

        return Response.ok(new GenericResponse<>(data, "API Server is working.", 200)).build();

    }


}
