package com.sales.infrastructure.rest.exception;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Provider
@ApplicationScoped
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof IllegalArgumentException) {
            return buildResponse(Response.Status.BAD_REQUEST, exception.getMessage());
        }

        if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) exception;
            String errors = cve.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            return buildResponse(Response.Status.BAD_REQUEST, errors);
        }

        return buildResponse(Response.Status.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro inesperado: " + exception.getMessage());
    }

    private Response buildResponse(Response.Status status, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status.getStatusCode());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        return Response.status(status).entity(errorResponse).build();
    }
}
