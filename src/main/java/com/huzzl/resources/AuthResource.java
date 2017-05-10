package com.huzzl.resources;

import com.huzzl.core.*;
import com.huzzl.resources.response.AuthResponse;
import com.huzzl.resources.response.GenericResponse;
import com.huzzl.service.AuthService;
import io.dropwizard.hibernate.UnitOfWork;
import org.jose4j.jwt.consumer.JwtContext;
import redis.clients.jedis.Jedis;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Path("/1.0/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private AuthService authService;

    private Map<String, Object> data;
    private String errorMessage = "";

    public AuthResource(AuthService authService) {
        this.authService = authService;
    }

    @POST
    @Path("/register")
    @UnitOfWork
    public Response registerUser(@Valid Users u, @Context Jedis jedis) {

        /**
         *  Check whether the email already exists in the database
         */

        data = new HashMap();

        Users oldUser = authService.findUserByEmailAddress(u.getEmailAddress());

        if(oldUser != null) {
            errorMessage = "Email address is already been used.";
        } else {

            try {

                /**
                 * Create new user instance
                 */
                Users newUser = authService.createNewUser(u);

                authService.createNewCredentials(u.getPassword(), newUser);

                /**
                 * Generate jwt token and add it to response container
                 * then saved generated access token to redis
                 */

                String token = authService.generateJwtToken(newUser, "loggedUser");
                Long expires = authService.getJwtExpiration();

                jedis.setex("tkn-" + newUser.getId()+"-"+expires, 3600, "1");

                newUser.setPassword(null);

                return Response.ok(
                        new AuthResponse<Users>(
                                newUser,
                                "User has been successfully created.",
                                token,
                                expires.toString(),
                                Response.Status.OK.getStatusCode(),
                                true
                        )
                ).build();

            } catch (Exception e) {
                // LOG EXCEPTION HERE
                errorMessage = "INTERNAL_ERROR";
            }
        }

        return Response.ok(new GenericResponse<>(data, errorMessage, 200, false)).build();
    }

    @POST
    @Path("/login")
    @UnitOfWork
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@FormParam("username") String username, @FormParam("password") String password, @Context Jedis jedis) {

        data = new HashMap();

        try {

            UserLogin login = authService.findUserLoginByEmailAddress(username);

            if(login != null) {

                PasswordStorage pass = new PasswordStorage();

                if(pass.verifyPassword(password, login.getPasswordHash()) == true) {

                    /**
                     * Generate jwt token and add it to response container
                     * then saved generated access token to redis
                     */

                    String token = authService.generateJwtToken(login.user, "loggedUser");
                    Long expires = authService.getJwtExpiration();

                    jedis.setex("tkn-" + login.user.getId() + "-" + expires, 3600, "1");

                    return Response.ok(new AuthResponse<Users>(
                       login.user,
                       "Authentication successful",
                       token,
                       expires.toString(),
                       Response.Status.OK.getStatusCode(),
                       true
                    )).build();
                }
            }

            errorMessage = "Invalid credentials. Login failed.";

        } catch (Exception e) {
            // LOG EXCEPTION HERE
            errorMessage = "INTERNAL_ERROR";
        }

        return Response.ok(new GenericResponse<>(data, errorMessage, 200, false)).build();
    }

    @POST
    @Path("/logout")
    @UnitOfWork
    public Response logout(@Valid GenericSerializer serializer, @Context Jedis jedis) {

        data = new HashMap();

        try {
            String key = "tkn-"+serializer.getUserId() + "-" + serializer.getExpires();
            String isDeleted = jedis.del(key).toString();

            if(isDeleted.equalsIgnoreCase("1")) {
                return Response.ok(new GenericResponse<>(data, "Successfully logged out.", 200, true)).build();
            }

        } catch (Exception e) {
            // LOG ERROR HERE
            errorMessage = "INTERNAL_ERROR";
        }

        errorMessage = "Token does not exists.";

        return Response.ok(new GenericResponse<>(data, errorMessage, 200, false)).build();
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
