package com.huzzl.resources;

import com.huzzl.core.AuthUser;
import com.huzzl.core.Task;
import com.huzzl.core.Users;
import com.huzzl.db.TaskDAO;
import com.huzzl.db.UsersDAO;
import com.huzzl.resources.response.GenericResponse;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

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
    @UnitOfWork
    @PermitAll
    public Response createTask(@Context SecurityContext context, @Valid Task task) {

        AuthUser auth = (AuthUser) context.getUserPrincipal();

        try {
            Users user = usersDao.findById(auth.getId());
            if(user != null) {
                Task newTask = (Task) taskDao.create(new Task(task.getTaskTitle(), task.getTaskDescription(), 1, user));

                Map<String, Object> hm = new HashMap();

                hm.put("test", newTask);
                hm.put("test2", newTask);
                hm.put("test3", newTask);

                return Response.ok(new GenericResponse<Task>(hm)).build();
            }

            System.out.println("INTERNAL_ERRROR");
            throw new WebApplicationException("INTERNAL_ERROR", Response.Status.BAD_REQUEST);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new WebApplicationException(e.getCause(), Response.Status.BAD_REQUEST);
        }
    }






    @GET
    @Path("/sample-resource-template")
    public Response getResonseTemplate(@Auth Principal user) {
        return Response.ok("ASDADAS").build();
    }
}
