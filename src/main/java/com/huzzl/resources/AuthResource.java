package com.huzzl.resources;

import com.huzzl.core.AuthUser;
import com.huzzl.core.PasswordStorage;
import com.huzzl.core.UserLogin;
import com.huzzl.core.Users;
import com.huzzl.resources.response.AuthResponse;
import com.huzzl.resources.response.GenericResponse;
import com.huzzl.service.AuthService;
import io.dropwizard.hibernate.UnitOfWork;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.HashMap;
import java.util.Map;


@Path("/1.0/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private AuthService authService;
    private Map<String, Object> data;

    public AuthResource(AuthService authService) {
        this.authService = authService;
    }

    @POST
    @Path("/register")
    @UnitOfWork
    public Response registerUser(@Valid Users u) {

        /**
         *  Check whether the email already exists in the database
         */

        data = new HashMap();

        String errorMessage = "INTERNAL_ERROR";

        Users oldUser = authService.findUserByEmailAddress(u.getEmailAddress());

        if(oldUser != null) {
            errorMessage = "Email address is already been used.";
        } else {

            try {

                /**
                 * Create new user
                 */
                Users newUser = authService.createNewUser(u);

                authService.createNewCredentials(u.getPassword(), newUser);

                /**
                 * Generate jwt token and add it to response container
                 */

                String token = authService.generateJwtToken(newUser, "loggedUser");

                return Response.ok(
                        new AuthResponse<Users>(
                                newUser,
                                "Registration successful!",
                                token,
                                Response.Status.OK.getStatusCode())
                ).build();

            } catch (Exception e) {
                // LOG EXCEPTION HERE
            }
        }

        return Response.ok(new GenericResponse<>(data, errorMessage, 200, false)).build();
    }

    @POST
    @Path("/login")
    @UnitOfWork
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@FormParam("username") String username, @FormParam("password") String password) {

        data = new HashMap();

        try {

            UserLogin login = authService.findUserLoginByEmailAddress(username);

            if(login != null) {

                PasswordStorage pass = new PasswordStorage();

                if(pass.verifyPassword(password, login.getPasswordHash()) == true) {

                    String token = authService.generateJwtToken(login.user, "loggedUser");

                    return Response.ok(new AuthResponse<Users>(
                       login.user,
                       "Authentication successful",
                       token,
                       Response.Status.OK.getStatusCode()
                    )).build();
                }
            }

        } catch (Exception e) {
            // LOG EXCEPTION HERE
        }

        return Response.ok(new GenericResponse<>(data, "Invalid Credentials", 200, false)).build();

    }


    @GET
    @Path("/sample-response-template")
    @RolesAllowed("DEFAULT")
    public Response getResponseTemplate(@Context SecurityContext context) {

        AuthUser user = (AuthUser) context.getUserPrincipal();

        System.out.print("User : " + user.getId() + " / "  + user.getName() + " / " + user.getEmailAddress() + " | NIL");
        return Response.ok("ASDADAS").build();
    }

}
