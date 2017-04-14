package com.huzzl.resources;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.huzzl.core.AuthUser;
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
    public AuthResponse<Users> registerUser(@Valid Users u) {

        /**
         *  Check whether the email already exists in the database
         */
        Users oldUser = usersDao.findUserByEmailAddress(u.getEmailAddress());

        if(oldUser != null) {
            throw new WebApplicationException("Email address already exists", Response.Status.BAD_REQUEST);
        } else {

            /**
             * Register new user
             */
            Users newUser = usersDao.create(u);

            try {
                userLoginDao.create(new UserLogin( u.getPassword(), newUser ));
            } catch (PasswordStorage.CannotPerformOperationException e) {
                throw new WebApplicationException(e.getCause(), Response.Status.BAD_REQUEST);
            }

            return new AuthResponse<Users>(newUser, "Success");
        }
    }

    @GET
    @Path("/login")
    public Response login() {

        PasswordStorage pass = new PasswordStorage();

        try {
            String correctHash = pass.createHash("asd");
            Boolean isCorrect  = pass.verifyPassword("asd", correctHash);

            System.out.println("HASH VALUE : " + correctHash);
            System.out.println("VERIFY PASSWORD : " + isCorrect);

        } catch (PasswordStorage.CannotPerformOperationException e) {
            System.out.println(e.getMessage().toString());
            e.printStackTrace();
        } catch (PasswordStorage.InvalidHashException e) {
            e.printStackTrace();
        }

        return Response.ok().entity("asdfdsafsadfasdfasdf").build();
    }

    @GET
    @Path("generate-valid-token")
    public Map<String, String> generateValidToken() {
        final JwtClaims claims = new JwtClaims();
        claims.setSubject("good-guy");
        claims.setExpirationTimeMinutesInTheFuture(30);
        claims.setClaim("user_id", "1");
        claims.setClaim("role", "Administrator");
        claims.setClaim("firstname", "Leo");
        claims.setClaim("lastname", "Diaz");
        claims.setClaim("email_address", "leoangelo.diaz@gmail.com");

        final JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setAlgorithmHeaderValue(HMAC_SHA256);
        jws.setKey(new HmacKey(tokenSecret));
        jws.setDoKeyValidation(false);

        try {
            return Collections.singletonMap("token", jws.getCompactSerialization());
        }
        catch (JoseException e) { throw Throwables.propagate(e); }
    }

    @GET
    @Path("/check-token")
    public Map<String, Object> get(@Auth Principal user) {
        return ImmutableMap.<String, Object>of("username", user.getName(), "id", ((AuthUser) user).getId());
    }

    @GET
    @Path("/hash")
    public Response getResponse() {
        return Response.ok().entity("").build();
    }

}
