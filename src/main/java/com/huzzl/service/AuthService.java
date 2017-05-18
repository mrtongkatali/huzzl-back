package com.huzzl.service;

import com.huzzl.core.AuthUser;
import com.huzzl.core.UserLogin;
import com.huzzl.core.Users;
import com.huzzl.db.UserLoginDAO;
import com.huzzl.db.UsersDAO;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.keys.HmacKey;

import static org.jose4j.jws.AlgorithmIdentifiers.HMAC_SHA256;

public class AuthService {

    private final byte[] tokenSecret;

    protected UserLoginDAO userLoginDao;
    protected UsersDAO usersDao;

    public AuthService(UserLoginDAO userLoginDao, UsersDAO usersDao, byte[] tokenSecret) {
        this.userLoginDao       = userLoginDao;
        this.usersDao           = usersDao;
        this.tokenSecret        = tokenSecret;
    }

    public Users findUserById(Long id) {
        Users u = usersDao.findById(id);
        return (u == null ? null : u);
    }

    public Users findUserByEmailAddress(String email) {
        Users u = usersDao.findUserByEmailAddress(email);
        return (u == null ? null : u);
    }

    public Users createNewUser(Users user) {
        return usersDao.create(user);
    }

    public UserLogin findUserLoginByEmailAddress(String username) {
        UserLogin login = userLoginDao.findUserByEmailAddress(username);
        return (login == null ? null : login);
    }

    public UserLogin createNewCredentials(String password, Users user) throws Exception {
        return userLoginDao.create(new UserLogin(password, user));
    }

    public Boolean checkOwnership(AuthUser user, Long user_id) {
        return (user.getId() == user_id);
    }

    public String generateJwtToken(Users user, String subject) throws Exception {

        final JwtClaims claims = new JwtClaims();
        claims.setSubject(subject);
        claims.setExpirationTimeMinutesInTheFuture(60);
        claims.setClaim("user_id", user.getId());
        claims.setClaim("roles", "DEFAULT");
        claims.setClaim("firstname", user.getFirstName());
        claims.setClaim("lastname", user.getLastName());
        claims.setClaim("email_address", user.getEmailAddress());

        final JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setAlgorithmHeaderValue(HMAC_SHA256);
        jws.setKey(new HmacKey(tokenSecret));
        jws.setDoKeyValidation(false);

        return jws.getCompactSerialization();

    }

    public Long getJwtExpiration() throws Exception {
        final JwtClaims claims = new JwtClaims();
        claims.setExpirationTimeMinutesInTheFuture(60);

        return claims.getExpirationTime().getValueInMillis();

    }
}
