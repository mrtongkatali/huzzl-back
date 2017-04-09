package com.huzzl.resources;

import com.huzzl.core.Users;
import com.huzzl.db.UsersDAO;
import com.huzzl.resources.response.AuthResponse;
import io.dropwizard.hibernate.UnitOfWork;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/1.0/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private final UsersDAO usersDao;

    public AuthResource(UsersDAO usersDao) { this.usersDao = usersDao; }

    @POST
    @Path("/create")
    @UnitOfWork
    public AuthResponse<Users> getResponse(@Valid Users u)  {

        Users oldUser = usersDao.findUserByEmailAddress(u.getEmailAddress());

        if(oldUser != null) {
            throw new WebApplicationException("Email address already exists", Response.Status.BAD_REQUEST);
        } else {
            Users newUser = usersDao.create(u);
            return new AuthResponse<Users>(newUser, "Success");
        }
    }

}
