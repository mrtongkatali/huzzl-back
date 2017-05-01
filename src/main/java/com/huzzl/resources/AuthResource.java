package com.huzzl.resources;

import com.huzzl.core.AuthUser;
import com.huzzl.core.PasswordStorage;
import com.huzzl.core.UserLogin;
import com.huzzl.core.Users;
import com.huzzl.resources.response.AuthResponse;
import com.huzzl.service.AuthService;
import io.dropwizard.hibernate.UnitOfWork;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;


@Path("/1.0/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private AuthService authService;

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
        Users oldUser = authService.findUserByEmailAddress(u.getEmailAddress());

        if(oldUser != null) {
            throw new WebApplicationException("Email address already exists", Response.Status.BAD_REQUEST);
        } else {

            /**
             * Create new user
             */
            Users newUser = authService.createNewUser(u);

            try {

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
                throw new WebApplicationException("INTERNAL_ERROR", Response.Status.BAD_REQUEST);
            }
        }
    }

    @POST
    @Path("/login")
    @UnitOfWork
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@FormParam("username") String username, @FormParam("password") String password) {

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

            throw new WebApplicationException("Invalid credentials.", Response.Status.BAD_REQUEST);

        } catch (Exception e) {
            throw new WebApplicationException("Invalid credentials.", Response.Status.BAD_REQUEST);
        }

    }


    @GET
    @Path("/sample-response-template")
    @RolesAllowed("DEFAULT")
    public Response getResonseTemplate(@Context SecurityContext context) {

        AuthUser user = (AuthUser) context.getUserPrincipal();

        System.out.print("User : " + user.getId() + " / "  + user.getName() + " / " + user.getEmailAddress() + " | NIL");
        return Response.ok("ASDADAS").build();
    }

}
