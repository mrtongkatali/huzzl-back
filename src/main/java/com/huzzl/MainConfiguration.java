package com.huzzl;

import com.bendb.dropwizard.redis.JedisFactory;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;

public class MainConfiguration extends Configuration {

    @NotEmpty
    private String template;

    @NotEmpty
    private String defaultName = "Stranger";

    @NotEmpty
    private String jwtTokenSecret = "dfwzsdzwh823zebdwdz772632gdsbd";

    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory database = new DataSourceFactory();

    @Valid
    @NotNull
    @JsonProperty("flyway")
    private FlywayFactory flywayFactory = new FlywayFactory();

    @NotNull
    @JsonProperty
    private JedisFactory redis;

    public JedisFactory getJedisFactory() { return redis; }

    public void setJedisFactory(JedisFactory jedisFactory) { this.redis = jedisFactory; }

    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    public FlywayFactory getFlywayFactory() {
        return flywayFactory;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public String getTemplate() {
        return template;
    }

    public byte[] getJwtTokenSecret() throws UnsupportedEncodingException {
        return jwtTokenSecret.getBytes("UTF-8");
    }
}
