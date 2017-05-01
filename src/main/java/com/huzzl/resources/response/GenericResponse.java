package com.huzzl.resources.response;

import java.util.HashMap;
import java.util.Map;


public class GenericResponse<T> {

    Map<String, Object> data = new HashMap<>();
    private String message;
    private Integer code;

    public GenericResponse(Map<String, Object> map, String message, Integer code) {
        this.data       = map;
        this.message    = message;
        this.code       = code;
    }

    public Map<String, Object> getData() { return data; }
    public String getMessage() { return message; }
    public Integer getCode() { return code; }
}
