package com.huzzl.resources.response;

import java.util.HashMap;
import java.util.Map;

public class AuthResponse<T> {

    Map<String, Object> result = new HashMap<>();
    private String message;
    private Integer code;
    private Boolean isSuccessful;

    public AuthResponse(T user, String message, String token, String expires, Integer code, Boolean isSuccessful) {
        this.message        = message;
        this.code           = code;
        this.isSuccessful   = isSuccessful;

        result.put("user", user);
        result.put("token", token);
        result.put("expires", expires);
    }

    public Map<String, Object> getResult() { return result; }
    public String getMessage() { return message; }
    public Integer getCode() { return code; }
    public Boolean getIsSuccessful() { return isSuccessful; }
}
