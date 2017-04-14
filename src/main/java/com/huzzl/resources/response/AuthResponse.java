package com.huzzl.resources.response;

import java.util.HashMap;
import java.util.Map;

public class AuthResponse<T> {

    Map<String, Object> users = new HashMap<>();

    public AuthResponse(T user, String message) {

        users.put("message", message);
        users.put("data", user);
    }

    public Map<String, Object> getUsers() { return users; }
}
