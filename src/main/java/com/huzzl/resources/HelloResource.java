package com.huzzl.resources;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huzzl.db.TaskDAO;
import com.huzzl.resources.response.GenericResponse;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class HelloResource {

    private final TaskDAO taskDao;

    private ObjectMapper mapper;
    private ObjectNode res;
    private ObjectNode childNode;

    public HelloResource(TaskDAO taskDao) {
        this.taskDao = taskDao;
    }

    @GET
    @UnitOfWork
    @Timed
    public Response getResponse(@PathParam("name") String name) {

        Map<String, Object> data = new HashMap();

        return Response.ok(new GenericResponse<>(data, "API Server is working.", 200)).build();

    }


}
