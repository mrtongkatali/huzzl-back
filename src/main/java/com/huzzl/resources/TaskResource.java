package com.huzzl.resources;

import com.huzzl.core.AuthUser;
import com.huzzl.core.Task;
import com.huzzl.core.Users;
import com.huzzl.resources.response.GenericResponse;
import com.huzzl.service.AuthService;
import com.huzzl.service.TaskService;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.*;

import javax.annotation.security.RolesAllowed;
import javax.management.Query;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/1.0/task")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Task (1.0)")

public class TaskResource {

    private AuthService authService;
    private TaskService taskService;

    private Map<String, Object> data;
    private String errorMessage = "";

    public TaskResource(AuthService authService, TaskService taskService) {
        this.authService    = authService;
        this.taskService    = taskService;
    }

    @POST
    @UnitOfWork
    @RolesAllowed({"DEFAULT", "ADMIN"})
    @ApiOperation(
        value = "Endpoint for creating task",
        notes = "Returns the task object when successfully created.",
        response = Task.class
    )
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Missing required fields"),
        @ApiResponse(code = 401, message = "Missing auth token in header")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "JWT Authentication token", required = true, dataType = "string", paramType = "header")
    })
    public Response createTask(@Context SecurityContext context, @Valid Task task) {

        data = new HashMap();

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

                data.put("task", newTask);

                return Response.ok(new GenericResponse<Task>(data, "successful", 200, true)).build();
            } else {
                errorMessage = "User does not exists. Task has not been saved.";
            }

        } catch (Exception e) {
            // LOG EXCEPTION HERE
            errorMessage = "INTERNAL_ERROR";
        }

        return Response.ok(new GenericResponse<>(data, errorMessage, 200, false)).build();
    }

    @PUT
    @Path("/{id}")
    @UnitOfWork
    @RolesAllowed({"DEFAULT", "ADMIN"})
    @ApiOperation(
        value = "Endpoint for updating task",
        notes = "Returns the task object when successfully updated.",
        response = Task.class
    )
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Missing required fields"),
        @ApiResponse(code = 401, message = "Missing auth token in header")
    })
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "JWT Authentication token", required = true, dataType = "string", paramType = "header")
    })
    public Response updateTask(@Context SecurityContext context, @Valid Task task, @PathParam("id") Long id) {

        data = new HashMap();

        AuthUser auth = (AuthUser) context.getUserPrincipal();

        try {
            Task existingTask = taskService.findTaskById(id);

            if(existingTask != null) {

                errorMessage = "You have no permission to update the data.";

                Boolean hasPermission = authService.checkOwnership(auth, existingTask.getUser().getId());

                if(hasPermission) {

                    task.setId(id);
                    task.setUser(existingTask.getUser());

                    taskService.update(task);

                    data.put("task", task);

                    return Response.ok(new GenericResponse<Task>(data, "Successful", 200, true)).build();
                }

            } else {
                errorMessage = "Task does not exists. Update failed.";
            }

        } catch (Exception e) {
            // LOG EXCEPTION HERE
            errorMessage = "INTERNAL_ERROR";
        }

        return Response.ok(new GenericResponse<>(data, errorMessage, 200, false)).build();
    }

    @GET
    @UnitOfWork
    @RolesAllowed({"DEFAULT", "ADMIN"})
    @ApiOperation(
        value = "Endpoint for getting all tasks by specific user",
        notes = "Default returned data is 10",
        response = Task.class
    )
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "JWT Authentication token", required = true, dataType = "string", paramType = "header")
    })
    public Response getTaskByUserId(
        @Context SecurityContext context,
        @QueryParam("status") Integer status,
        @QueryParam("page") Integer page,
        @QueryParam("count") Integer count
    ) {

        data = new HashMap();

        try {
            AuthUser user = (AuthUser) context.getUserPrincipal();

            count   = ( count == null ? 10 : count);
            page    = ( page == null ? 0 : page);
            status  = ( status == null ? 1 : status);

            Number rows = taskService.countAllTaskByUserId(user.getId(), status);
            List<Task> task = taskService.findAllTaskByUserId(user.getId(), status, count, page);

            data.put("task", task);
            data.put("total_count", rows);

            return Response.ok(new GenericResponse<Task>(data, "Successful", 200, true)).build();

        } catch (Exception e) {
            // LOG EXCEPTION HERE
            errorMessage = "INTERNAL_ERROR";
        }

        return Response.ok(new GenericResponse<>(data, errorMessage, 200, false)).build();
    }

}
