package com.huzzl.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.huzzl.core.AuthUser;
import com.huzzl.core.GenericSerializer;
import io.dropwizard.auth.Authenticator;
import org.apache.commons.lang3.StringUtils;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtContext;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.util.*;

public class JwtAuthenticator implements Authenticator<JwtContext, AuthUser> {

    /**
     * @param context
     * @return Boolean
     */
    public Boolean validateToken(JwtContext context) {

        try {

            /**
             * @Implementation: Directly parse the content of the yml configuration file since currently
             * there is no way to inject the configuration class to authenticator class (not sure how to do it)
             */
            final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

            String filename = (System.getenv("ENVI") == "prod" ? "production.yml" : "development.yml");
            Map<String, String> redis = mapper.readValue(new File("src/main/config/"+filename), GenericSerializer.class).getRedis();

            // Reads the string as 192.168.1.1:8888 - we need to get the host and port separately.
            String[] hostport = redis.get("endpoint").split(":");

            Jedis jedis = new Jedis(hostport[0], Integer.parseInt(hostport[1]) );
            jedis.auth(redis.get("password"));
            jedis.connect();

            JwtClaims claims = context.getJwtClaims();

            Long user_id  = Long.parseLong(claims.getClaimValue("user_id").toString());
            Long expires  = Long.parseLong(claims.getClaimValue("exp").toString());

            String hasSession = jedis.get("tkn-"+user_id+"-"+expires+"000");

            jedis.disconnect();
            jedis.close();

            if(hasSession.equalsIgnoreCase("1")) {
                return true;
            }

        } catch(Exception e) {
            // LOG ERROR HERE
        }

        return false;
    }

    public Optional<AuthUser> authenticate(JwtContext context)  {

        /**
         * Added security layer - Double checks the validity of token on the server so that when
         * the user signs out (on the frontend), that jwt token will no longer valid.
         */
        if(!validateToken(context) ) {
            return Optional.empty();
        }

        try {

            JwtClaims claims = context.getJwtClaims();

            final Long user_id          = Long.parseLong(claims.getClaimValue("user_id").toString());
            final String claim_roles    = claims.getClaimValue("roles").toString();
            final Set<String> roles     = new HashSet<String>(Arrays.asList(StringUtils.join(claim_roles, ", ").replaceAll(", $", "")));

            return Optional.of(new AuthUser(
                    user_id,
                    claims.getStringClaimValue("firstname"),
                    claims.getStringClaimValue("lastname"),
                    claims.getStringClaimValue("email_address"),
                    roles
                ));
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }

}