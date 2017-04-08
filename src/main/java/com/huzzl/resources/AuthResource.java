package com.huzzl.resources;

import com.huzzl.db.UsersDAO;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    private final UsersDAO usersDao;

    public AuthResource(UsersDAO usersDao) { this.usersDao = usersDao; }


}
