package com.huzzl.resources.response;

import javax.ws.rs.WebApplicationException;

public class ExceptionResponse extends WebApplicationException {

    private String message;

    public ExceptionResponse(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() { return message; }
}


