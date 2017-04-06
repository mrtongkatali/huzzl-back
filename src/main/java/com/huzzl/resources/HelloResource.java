package com.huzzl.resources;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.huzzl.core.Task;
import com.huzzl.db.TaskDAO;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
public class HelloResource {

    private final TaskDAO taskDao;

    ObjectMapper mapper;
    ObjectNode res;
    ObjectNode childNode;

    public HelloResource(TaskDAO taskDao) {
        this.taskDao = taskDao;
    }

    @GET
    @Timed
    @UnitOfWork
    public List<Task> sayHelloWorld() {
        return taskDao.getAllTask();
    }

    @GET
    @Path("{id}")
    @UnitOfWork
    public Response getTaskById(@PathParam("id") long id) {
        Task task = taskDao.findTaskById(id);

        mapper      = new ObjectMapper();
        childNode   = mapper.createObjectNode();

        if(task != null) {
            childNode.putPOJO("task", task);
        }

        res = mapper.createObjectNode();
        res.put("status", "success");
        res.putPOJO("data", childNode);

        return Response.ok(res).build();
    }

    @GET
    @Path("/sample/{name}")
    @UnitOfWork
    public Response getResponse(@PathParam("name") String name) {
        mapper      = new ObjectMapper();
        childNode   = mapper.createObjectNode();

        childNode.putPOJO("task", taskDao.getAllTask());
        childNode.putPOJO("count", taskDao.getAllTask().size());

        res = mapper.createObjectNode();
        res.put("status", "success");
        res.putPOJO("data", childNode);

        return Response.ok(res).build();
    }
}
