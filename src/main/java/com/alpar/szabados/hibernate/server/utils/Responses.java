package com.alpar.szabados.hibernate.server.utils;

import javax.ws.rs.core.Response.Status;

import static javax.ws.rs.core.Response.Status.*;

public enum Responses {
    OK(Status.OK, ""),
    SERVER_ERROR(INTERNAL_SERVER_ERROR, "SERVER ERROR OCCURRED "),
    USER_ALREADY_EXISTS(BAD_REQUEST, "USER ALREADY EXISTS"),
    USER_NOT_FOUND(BAD_REQUEST, "USER NOT FOUND"),
    INVALID_PASSWORD(UNAUTHORIZED, "INVALID PASSWORD"),
    WRONG_PASSWORD(UNAUTHORIZED, "WRONG PASSWORD");

    private Status status;
    private String message;

    Responses(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
