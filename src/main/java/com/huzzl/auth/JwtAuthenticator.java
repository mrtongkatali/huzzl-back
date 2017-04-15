package com.huzzl.auth;

import com.huzzl.core.AuthUser;
import io.dropwizard.auth.Authenticator;
import org.jose4j.jwt.consumer.JwtContext;

import java.util.Optional;

public class JwtAuthenticator implements Authenticator<JwtContext, AuthUser> {

    public Optional<AuthUser> authenticate(JwtContext context)  {

        try {

            final Long user_id = Long.parseLong(context.getJwtClaims().getStringClaimValue("user_id"));
            return Optional.of(new AuthUser(
                    user_id,
                    context.getJwtClaims().getStringClaimValue("firstname"),
                    context.getJwtClaims().getStringClaimValue("lastname"),
                    context.getJwtClaims().getStringClaimValue("email_address"),
                    context.getJwtClaims().getStringClaimValue("roles")
                ));
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }

}
