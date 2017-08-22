package com.alpar.szabados.hibernate.server.utils;

import javax.ws.rs.core.Response;

public final class ResponseFactory {
    public static Response responseAndException(Responses responses, RuntimeException e) {
        return Response.status(responses.getStatus()).entity(responses.getMessage() + e).build();
    }

    public static Response responseAndMessage(Responses responses) {
        return Response.status(responses.getStatus()).entity(responses.getMessage()).build();
    }

    public static Response responseOkAndEntity(Responses responses, Object entity) {
        return Response.status(responses.getStatus()).entity(entity).build();
    }

    public static Response responseOk() {
        return Response.ok().build();
    }
}
