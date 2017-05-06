package com.huzzl.resources.response;

import java.util.HashMap;
import java.util.Map;

public class AuthResponse<T> {

    Map<String, Object> data = new HashMap<>();
    private String message;
    private Integer code;

    public AuthResponse(T user, String message, String token, Integer code) {
        this.message = message;
        this.code    = code;

        data.put("user", user);
        data.put("token", token);
    }

    public Map<String, Object> getData() { return data; }
    public String getMessage() { return message; }
    public Integer getCode() { return code; }
}
