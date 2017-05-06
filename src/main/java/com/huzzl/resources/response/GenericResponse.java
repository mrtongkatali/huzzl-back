package com.huzzl.resources.response;

import java.util.HashMap;
import java.util.Map;


public class GenericResponse<T> {

    Map<String, Object> result = new HashMap<>();
    private String message;
    private Integer code;

    public GenericResponse(Map<String, Object> map, String message, Integer code) {
        this.result     = map;
        this.message    = message;
        this.code       = code;
    }

    public Map<String, Object> getResult() { return result; }
    public String getMessage() { return message; }
    public Integer getCode() { return code; }
}
