package com.huzzl.resources;

import com.codahale.metrics.annotation.Timed;
import com.huzzl.core.Task;
import com.huzzl.db.TaskDAO;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
public class HelloResource {

    private final TaskDAO taskDao;

    public HelloResource(TaskDAO taskDao) {
        this.taskDao = taskDao;
    }

    @GET
    @Timed
    @UnitOfWork
    public List<Task> sayHelloWorld() {
        return taskDao.getAllTask();
    }


}
