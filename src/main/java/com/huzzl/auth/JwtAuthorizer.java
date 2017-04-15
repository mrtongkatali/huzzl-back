package com.huzzl.auth;

import com.huzzl.core.AuthUser;
import io.dropwizard.auth.Authorizer;

/**
 * Created by mrtongkatali on 15/04/2017.
 */
public class JwtAuthorizer implements Authorizer<AuthUser> {

    @Override
    public boolean authorize(AuthUser user, String role) {
        return user.getRoles() != null && user.getRoles().contains(role);
    }
}
