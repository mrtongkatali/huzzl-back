package com.huzzl.core;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.jackson.JsonSnakeCase;

import java.util.Map;

@JsonSnakeCase
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenericSerializer {

    @JsonProperty
    public Long user_id;

    @JsonProperty
    public String expires   = "";

    @JsonProperty
    private Map<String,String> redis;

    public Long getUserId() { return user_id; }
    public String getExpires() { return expires; }
    public Map<String,String> getRedis() { return redis; }
}
