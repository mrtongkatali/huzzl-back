package com.huzzl.resources;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huzzl.core.Task;
import com.huzzl.db.TaskDAO;
import com.huzzl.resources.response.HelloResponse;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/hello-world")
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
    @Path("/sample1/{name}")
    @UnitOfWork
    @Timed
    public Response getResponse(@PathParam("name") String name) {
        mapper      = new ObjectMapper();
        childNode   = mapper.createObjectNode();

        // http://stackoverflow.com/questions/32181395/jersey-custom-json-responses
        // http://stackoverflow.com/questions/13431986/how-to-create-custom-json-xml-responses-in-my-web-service
        // http://stackoverflow.com/questions/583973/jax-rs-jersey-how-to-customize-error-handling
        // http://www.makeinjava.com/jersey-bean-validation-rest-request-query-parameters-using-standard-annotation-example/
        // https://engineering.vena.io/2016/04/25/dont-initialize-your-entities/

        childNode.putPOJO("task", taskDao.getAllTask());
        childNode.putPOJO("count", taskDao.getAllTask().size());

        res = mapper.createObjectNode();
        res.put("status", "success");
        res.putPOJO("data", childNode);

        return Response.ok(taskDao.getAllTask()).build();
    }

    @GET
    @Path("/sample/task")
    @UnitOfWork
    @Timed
    public HelloResponse<Task> getAllTask() {
        List<Task> task = taskDao.getAllTask();
        return new HelloResponse<Task>(task);
    }

    @GET
    @Path("/sample/task/{id}")
    @UnitOfWork
    @Timed
    public HelloResponse<Task> getTaskById(@PathParam("id") Long id) {
        Task task = taskDao.findTaskById(id);

        //throw new WebApplicationException(new IllegalArgumentException("Date Header was not specified"), Response.Status.BAD_REQUEST);

        return new HelloResponse<Task>(task);
    }


}
