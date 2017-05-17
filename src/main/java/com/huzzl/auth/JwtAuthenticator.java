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

            String[] hostport;
            String host;
            String port;
            String password;

            String environment = System.getenv("ENVI");

            if (environment != null && environment == "prod") {
                host     = System.getenv("REDIS_ENDPOINT");
                port     = System.getenv("REDIS_PORT");
                password = System.getenv("REDIS_PASSWORD");

            } else {

                /**
                 * If on development, manually load/parse the yml config
                 */
                final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                Map<String, String> redis = mapper.readValue(new File("src/main/config/development.yml"), GenericSerializer.class).getRedis();

                // Reads the string as 192.168.1.1:8888 - we need to get the host and port separately.
                hostport = redis.get("endpoint").split(":");
                host = hostport[0];
                port = hostport[1];
                password = redis.get("password");
            }

            Jedis jedis = new Jedis(host, Integer.parseInt(port) );
            jedis.auth(password);
            jedis.connect();

            JwtClaims claims = context.getJwtClaims();

            Long user_id  = Long.parseLong(claims.getClaimValue("user_id").toString());
            Long expires  = Long.parseLong(claims.getClaimValue("exp").toString());

            String hasSession = jedis.get("tkn-"+user_id+"-"+expires+"000");

            System.out.println("\n\n #### hasSession :" + hasSession);

            jedis.disconnect();
            jedis.close();

            if(hasSession.equalsIgnoreCase("1")) {
                return true;
            }

        } catch(Exception e) {
            e.printStackTrace();
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