package com.huzzl.resources;

import com.huzzl.core.AuthUser;
import com.huzzl.core.Task;
import com.huzzl.core.Users;
import com.huzzl.resources.response.GenericResponse;
import com.huzzl.service.AuthService;
import com.huzzl.service.TaskService;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

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

    private AuthService authService;
    private TaskService taskService;

    public TaskResource(AuthService authService, TaskService taskService) {
        this.authService    = authService;
        this.taskService    = taskService;
    }

    @POST
    @UnitOfWork
    @RolesAllowed({"DEFAULT", "ADMIN"})
    public Response createTask(@Context SecurityContext context, @Valid Task task) {

        AuthUser auth = (AuthUser) context.getUserPrincipal();

        try {
            Users user = authService.findUserById(auth.getId());

            if(user != null) {
                Task newTask = taskService.createNewTask(
                        new Task(
                                task.getTaskTitle(),
                                task.getTaskDescription(),
                                1,
                                task.getSort(),
                                user
                        )
                );

                Map<String, Object> data = new HashMap();

                data.put("task", newTask);

                return Response.ok(new GenericResponse<Task>(data, "successful", 200)).build();
            }

            System.out.println("INTERNAL_ERROR");
            throw new WebApplicationException("INTERNAL_ERROR", Response.Status.BAD_REQUEST);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new WebApplicationException(e.getCause(), Response.Status.BAD_REQUEST);
        }
    }

    @PUT
    @Path("/{id}")
    @UnitOfWork
    @RolesAllowed({"DEFAULT", "ADMIN"})
    public Response updateTask(@Context SecurityContext context, @Valid Task task, @PathParam("id") Long id) {

        AuthUser auth = (AuthUser) context.getUserPrincipal();

        try {
            Task existingTask = taskService.findTaskById(id);

            if(existingTask != null) {

                task.setId(id);
                taskService.update(task);

                Map<String, Object> data = new HashMap();
                data.put("task", task);

                return Response.ok(new GenericResponse<Task>(data, "Successful", 200)).build();
            }

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
