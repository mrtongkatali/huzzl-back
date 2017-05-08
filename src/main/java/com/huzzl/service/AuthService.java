package com.huzzl.service;

import com.huzzl.core.JwtAccessToken;
import com.huzzl.core.UserLogin;
import com.huzzl.core.Users;
import com.huzzl.db.JwtAccessTokenDAO;
import com.huzzl.db.UserLoginDAO;
import com.huzzl.db.UsersDAO;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.keys.HmacKey;

import java.util.Date;

import static org.jose4j.jws.AlgorithmIdentifiers.HMAC_SHA256;

public class AuthService {

    private final byte[] tokenSecret;

    protected UserLoginDAO userLoginDao;
    protected UsersDAO usersDao;
    protected JwtAccessTokenDAO jwtAccessTokenDao;

    public AuthService(UserLoginDAO userLoginDao, UsersDAO usersDao, JwtAccessTokenDAO tokenDao, byte[] tokenSecret) {
        this.userLoginDao       = userLoginDao;
        this.usersDao           = usersDao;
        this.tokenSecret        = tokenSecret;
        this.jwtAccessTokenDao  = tokenDao;
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

    public JwtAccessToken saveAccessToken(String token, Date expires, Users user) throws Exception {
        return jwtAccessTokenDao.create(new JwtAccessToken(token, expires, user));
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

    public Date getJwtExpiration() throws Exception {
        final JwtClaims claims = new JwtClaims();
        claims.setExpirationTimeMinutesInTheFuture(60);

        return new java.util.Date(claims.getExpirationTime().getValueInMillis());

    }
}
