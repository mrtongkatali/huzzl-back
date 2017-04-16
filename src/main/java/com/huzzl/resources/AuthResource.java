package com.huzzl.resources;

import com.huzzl.core.PasswordStorage;
import com.huzzl.core.UserLogin;
import com.huzzl.core.Users;
import com.huzzl.db.UserLoginDAO;
import com.huzzl.db.UsersDAO;
import com.huzzl.resources.response.AuthResponse;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.Principal;
import java.util.Collections;
import java.util.Map;

import static org.jose4j.jws.AlgorithmIdentifiers.HMAC_SHA256;

@Path("/1.0/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private final UsersDAO usersDao;
    private final UserLoginDAO userLoginDao;
    private final byte[] tokenSecret;

    public AuthResource(UsersDAO usersDao, UserLoginDAO userLoginDao, byte[] tokenSecret) {
        this.usersDao       = usersDao;
        this.userLoginDao   = userLoginDao;
        this.tokenSecret    = tokenSecret;
    }

    @POST
    @Path("/register")
    @UnitOfWork
    public Response registerUser(@Valid Users u) {

        /**
         *  Check whether the email already exists in the database
         */
        Users oldUser = usersDao.findUserByEmailAddress(u.getEmailAddress());

        if(oldUser != null) {
            throw new WebApplicationException("Email address already exists", Response.Status.BAD_REQUEST);
        } else {

            /**
             * Create new user
             */
            Users newUser = usersDao.create(u);

            try {

                userLoginDao.create(new UserLogin( u.getPassword(), newUser ));

                /**
                 * Generate jwt token and add it to response container
                 */

                String token = generateJwtAuthToken(newUser, "loggedUser");

                return Response.ok(
                        new AuthResponse<Users>(
                                newUser,
                                "Registration successful!",
                                token,
                                Response.Status.OK.getStatusCode())
                ).build();

            } catch (Exception e) {
                throw new WebApplicationException(e.getCause(), Response.Status.BAD_REQUEST);
            }
        }
    }

    @POST
    @Path("/login")
    @UnitOfWork
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@FormParam("username") String username, @FormParam("password") String password) {

        try {

            UserLogin login = userLoginDao.findUserByEmailAddress(username);

            if(login != null) {

                PasswordStorage pass = new PasswordStorage();

                if(pass.verifyPassword(password, login.getPasswordHash()) == true) {

                    String token = generateJwtAuthToken(login.user, "loggedUser");

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

    public String generateJwtAuthToken(Users u, String subject) {

        try {
            final JwtClaims claims = new JwtClaims();
            claims.setSubject(subject);
            claims.setExpirationTimeMinutesInTheFuture(30);
            claims.setClaim("user_id", u.getId());
            claims.setClaim("role", "default");
            claims.setClaim("firstname", u.getFirstName());
            claims.setClaim("lastname", u.getLastName());
            claims.setClaim("email_address", u.getEmailAddress());

            final JsonWebSignature jws = new JsonWebSignature();
            jws.setPayload(claims.toJson());
            jws.setAlgorithmHeaderValue(HMAC_SHA256);
            jws.setKey(new HmacKey(tokenSecret));
            jws.setDoKeyValidation(false);

            return jws.getCompactSerialization();

        } catch(Exception e) {
            throw new WebApplicationException("Failed to generate token", Response.Status.BAD_REQUEST);
        }
    }

    @GET
    @Path("/sample-response-template")
    public Response getResonseTemplate(@Auth Principal user) {
        return Response.ok("ASDADAS").build();
    }

}
