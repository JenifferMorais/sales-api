package com.sales.infrastructure.rest.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException with BAD_REQUEST status")
    void shouldHandleIllegalArgumentExceptionWithBadRequestStatus() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        Response response = exceptionHandler.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        assertThat(entity.get("status")).isEqualTo(400);
        assertThat(entity.get("error")).isEqualTo("Bad Request");
        assertThat(entity.get("message")).isEqualTo("Invalid argument");
    }

    @Test
    @DisplayName("Should handle ConstraintViolationException with validation messages")
    void shouldHandleConstraintViolationExceptionWithValidationMessages() {
        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);

        when(violation1.getMessage()).thenReturn("Field A is required");
        when(violation2.getMessage()).thenReturn("Field B must be valid");

        Set<ConstraintViolation<?>> violations = Set.of(violation1, violation2);
        ConstraintViolationException exception = new ConstraintViolationException(violations);

        Response response = exceptionHandler.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        assertThat(entity.get("status")).isEqualTo(400);
        assertThat(entity.get("error")).isEqualTo("Bad Request");

        String message = (String) entity.get("message");
        assertThat(message).contains("Field A is required");
        assertThat(message).contains("Field B must be valid");
    }

    @Test
    @DisplayName("Should handle ConstraintViolationException with single violation")
    void shouldHandleConstraintViolationExceptionWithSingleViolation() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Email is invalid");

        Set<ConstraintViolation<?>> violations = Set.of(violation);
        ConstraintViolationException exception = new ConstraintViolationException(violations);

        Response response = exceptionHandler.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        assertThat(entity.get("message")).isEqualTo("Email is invalid");
    }

    @Test
    @DisplayName("Should handle generic Exception with INTERNAL_SERVER_ERROR status")
    void shouldHandleGenericExceptionWithInternalServerErrorStatus() {
        Exception exception = new Exception("Unexpected error occurred");

        Response response = exceptionHandler.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        assertThat(entity.get("status")).isEqualTo(500);
        assertThat(entity.get("error")).isEqualTo("Internal Server Error");
        assertThat(entity.get("message")).isEqualTo("Ocorreu um erro inesperado: Unexpected error occurred");
    }

    @Test
    @DisplayName("Should handle RuntimeException as generic Exception")
    void shouldHandleRuntimeExceptionAsGenericException() {
        RuntimeException exception = new RuntimeException("Runtime error");

        Response response = exceptionHandler.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        assertThat(entity.get("message").toString()).contains("Runtime error");
    }

    @Test
    @DisplayName("Should handle exception with null message")
    void shouldHandleExceptionWithNullMessage() {
        Exception exception = new Exception((String) null);

        Response response = exceptionHandler.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        assertThat(entity.get("message").toString()).contains("Ocorreu um erro inesperado");
    }

    @Test
    @DisplayName("Should build response with correct structure")
    void shouldBuildResponseWithCorrectStructure() {
        IllegalArgumentException exception = new IllegalArgumentException("Test error");

        Response response = exceptionHandler.toResponse(exception);

        @SuppressWarnings("unchecked")
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();

        assertThat(entity).containsKeys("status", "error", "message");
        assertThat(entity.get("status")).isInstanceOf(Integer.class);
        assertThat(entity.get("error")).isInstanceOf(String.class);
        assertThat(entity.get("message")).asString().isNotEmpty();
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException with empty message")
    void shouldHandleIllegalArgumentExceptionWithEmptyMessage() {
        IllegalArgumentException exception = new IllegalArgumentException("");

        Response response = exceptionHandler.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        assertThat(entity.get("message")).isEqualTo("");
    }

    @Test
    @DisplayName("Should handle NullPointerException as generic exception")
    void shouldHandleNullPointerExceptionAsGenericException() {
        NullPointerException exception = new NullPointerException("Null value encountered");

        Response response = exceptionHandler.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        assertThat(entity.get("message").toString()).contains("Null value encountered");
    }

    @Test
    @DisplayName("Should format constraint violations with comma separator")
    void shouldFormatConstraintViolationsWithCommaSeparator() {
        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
        ConstraintViolation<?> violation3 = mock(ConstraintViolation.class);

        when(violation1.getMessage()).thenReturn("Error 1");
        when(violation2.getMessage()).thenReturn("Error 2");
        when(violation3.getMessage()).thenReturn("Error 3");

        Set<ConstraintViolation<?>> violations = Set.of(violation1, violation2, violation3);
        ConstraintViolationException exception = new ConstraintViolationException(violations);

        Response response = exceptionHandler.toResponse(exception);

        @SuppressWarnings("unchecked")
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        String message = (String) entity.get("message");

        assertThat(message).contains("Error 1");
        assertThat(message).contains("Error 2");
        assertThat(message).contains("Error 3");
        assertThat(message).contains(",");
    }

    @Test
    @DisplayName("Should return correct HTTP status code for BAD_REQUEST")
    void shouldReturnCorrectHttpStatusCodeForBadRequest() {
        IllegalArgumentException exception = new IllegalArgumentException("Bad request");

        Response response = exceptionHandler.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(400);
    }

    @Test
    @DisplayName("Should return correct HTTP status code for INTERNAL_SERVER_ERROR")
    void shouldReturnCorrectHttpStatusCodeForInternalServerError() {
        Exception exception = new Exception("Internal error");

        Response response = exceptionHandler.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(500);
    }
}
