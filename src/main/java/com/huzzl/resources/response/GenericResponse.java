package com.huzzl.resources.response;

import java.util.HashMap;
import java.util.Map;


public class GenericResponse<T> {

    Map<String, Object> data = new HashMap<>();
    private String message;
    private Integer code;

    public GenericResponse(Map<String, Object> map) {
        this.data = map;
    }

    public Map<String, Object> getData() { return data; }
}
