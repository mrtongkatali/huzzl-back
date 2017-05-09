package com.huzzl.resources;

import com.codahale.metrics.annotation.Timed;
import com.huzzl.resources.response.GenericResponse;
import io.dropwizard.hibernate.UnitOfWork;
import redis.clients.jedis.Jedis;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
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

        Map<String, Object> result = new HashMap();

        return Response.ok(new GenericResponse<>(result, "API Server is working.", 200, true)).build();
    }


    @GET
    @Path("/hello-jedis")
    @UnitOfWork
    @Timed
    public Response getJedisResponse(@PathParam("name") String name, @Context Jedis jedis) {

        Map<String, Object> result = new HashMap();

        String test = jedis.get("post");



        return Response.ok(new GenericResponse<>(result, test, 200, true)).build();
    }



}
