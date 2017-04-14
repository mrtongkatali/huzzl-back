package com.huzzl.auth;

import com.huzzl.core.AuthUser;
import io.dropwizard.auth.Authenticator;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.JwtContext;

import java.util.Optional;

public class JwtAuthenticator implements Authenticator<JwtContext, AuthUser> {

    @Override
    public Optional<AuthUser> authenticate(JwtContext context)  {

        try {
            final String email_address = context.getJwtClaims().getClaimValue("user_id").toString();

            //final String subject = context.getJwtClaims().getSubject();
            //System.out.println("## CLAIM ##" + context.getJwtClaims().getClaimValue("user_id"));

            return Optional.of(new AuthUser(1L, "asdas", "good-guy", "sdasdas"));
            //return Optional.empty();

        }
        catch (Exception e) {
            System.out.println("### ERROR ###");
            return Optional.empty();
        }


    }
}
