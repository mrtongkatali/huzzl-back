package com.huzzl.resources.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelloResponse<T> {
    Map<String, Object> tasks = new HashMap<>();

    public HelloResponse(List<T> list) {
        tasks.put("data", list);
        tasks.put("length", list.size());
    }

    public HelloResponse(T task) {
        tasks.put("data",task);
    }

    public Map<String, Object> getTasks() { return tasks; }
}
