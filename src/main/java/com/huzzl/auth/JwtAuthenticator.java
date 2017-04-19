package com.huzzl.auth;

import com.huzzl.core.AuthUser;
import io.dropwizard.auth.Authenticator;
import org.apache.commons.lang3.StringUtils;
import org.jose4j.jwt.consumer.JwtContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class JwtAuthenticator implements Authenticator<JwtContext, AuthUser> {

    public Optional<AuthUser> authenticate(JwtContext context)  {

        try {

            final Long user_id          = Long.parseLong(context.getJwtClaims().getClaimValue("user_id").toString());
            final String claim_roles    = context.getJwtClaims().getClaimValue("roles").toString();
            final Set<String> roles     = new HashSet<String>(Arrays.asList(StringUtils.join(claim_roles, ", ").replaceAll(", $", "")));

            return Optional.of(new AuthUser(
                    user_id,
                    context.getJwtClaims().getStringClaimValue("firstname"),
                    context.getJwtClaims().getStringClaimValue("lastname"),
                    context.getJwtClaims().getStringClaimValue("email_address"),
                    roles
                ));
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }

}
