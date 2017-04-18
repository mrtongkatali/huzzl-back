package com.huzzl.resources;

import com.huzzl.core.Task;
import com.huzzl.core.Users;
import com.huzzl.db.TaskDAO;
import com.huzzl.db.UsersDAO;
import io.dropwizard.auth.Auth;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.Principal;

@Path("/1.0/task")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TaskResource {

    private final UsersDAO usersDao;
    private final TaskDAO taskDao;

    public TaskResource(UsersDAO usersDao, TaskDAO taskDao) {
        this.usersDao = usersDao;
        this.taskDao = taskDao;
    }

    @POST
    @Path("")
    public Response createTask(@Auth Principal user) {
        return Response.ok("VALID USER").build();
    }


    @GET
    @Path("/sample-resource-template")
    public Response getResonseTemplate(@Auth Principal user) {
        return Response.ok("ASDADAS").build();
    }
}
