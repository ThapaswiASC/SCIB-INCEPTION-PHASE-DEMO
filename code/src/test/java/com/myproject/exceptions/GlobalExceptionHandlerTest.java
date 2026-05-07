package com.myproject.exceptions;

import com.myproject.controllers.TaskController;
import com.myproject.models.dtos.ErrorResponse;
import com.myproject.models.dtos.TaskCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    // ========== DATABASE CONNECTION EXCEPTION TESTS ==========

    @Test
    void handleDatabaseException_ReturnsServiceUnavailable() {
        DatabaseConnectionException exception = new DatabaseConnectionException("Database connection failed");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDatabaseException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("SERVICE_UNAVAILABLE", response.getBody().getErrorCode());
        assertEquals("Service temporarily unavailable, please try again later", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
        assertTrue(response.getBody().getDetails().contains("Database connection failed"));
    }

    @Test
    void handleDatabaseException_WithCause_ReturnsServiceUnavailable() {
        DatabaseConnectionException exception = new DatabaseConnectionException(
            "Database connection failed",
            new RuntimeException("Connection timeout")
        );

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDatabaseException(exception);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("SERVICE_UNAVAILABLE", response.getBody().getErrorCode());
    }

    // ========== TIMEOUT EXCEPTION TESTS ==========

    @Test
    void handleTimeoutException_ReturnsRequestTimeout() {
        TimeoutException exception = new TimeoutException("Request timed out");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTimeoutException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.REQUEST_TIMEOUT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("REQUEST_TIMEOUT", response.getBody().getErrorCode());
        assertEquals("Request timed out, please try again", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleTimeoutException_WithCause_ReturnsRequestTimeout() {
        TimeoutException exception = new TimeoutException(
            "Request timed out",
            new RuntimeException("Network timeout")
        );

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTimeoutException(exception);

        assertEquals(HttpStatus.REQUEST_TIMEOUT, response.getStatusCode());
        assertEquals("REQUEST_TIMEOUT", response.getBody().getErrorCode());
    }

    // ========== TASK NOT FOUND EXCEPTION TESTS ==========

    @Test
    void handleTaskNotFoundException_ReturnsNotFound() {
        TaskNotFoundException exception = new TaskNotFoundException("Task not found with id: 123");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTaskNotFoundException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("NOT_FOUND", response.getBody().getErrorCode());
        assertEquals("Task not found with id: 123", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleTaskNotFoundException_WithLongId_ReturnsNotFound() {
        TaskNotFoundException exception = new TaskNotFoundException(999L);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTaskNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("NOT_FOUND", response.getBody().getErrorCode());
        assertTrue(response.getBody().getMessage().contains("999"));
    }

    // ========== VALIDATION EXCEPTION TESTS ==========

    @Test
    void handleValidationException_ReturnsBadRequest() {
        List<String> errors = Arrays.asList("Title is required", "Description is required");
        ValidationException exception = new ValidationException("Validation failed", errors);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALIDATION_ERROR", response.getBody().getErrorCode());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
        assertEquals(2, response.getBody().getDetails().size());
        assertTrue(response.getBody().getDetails().contains("Title is required"));
        assertTrue(response.getBody().getDetails().contains("Description is required"));
    }

    @Test
    void handleValidationException_WithNoErrors_ReturnsBadRequest() {
        ValidationException exception = new ValidationException("Validation failed");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("VALIDATION_ERROR", response.getBody().getErrorCode());
        assertTrue(response.getBody().getDetails().isEmpty());
    }

    // ========== METHOD ARGUMENT NOT VALID EXCEPTION TESTS ==========

    @Test
    void handleMethodArgumentNotValid_ReturnsBadRequest() throws Exception {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("taskCreateRequest", "title", "Title is required");
        FieldError fieldError2 = new FieldError("taskCreateRequest", "description", "Description is required");
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        MethodParameter parameter = new MethodParameter(
            TaskController.class.getMethod("createTask", TaskCreateRequest.class), 0
        );
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMethodArgumentNotValid(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALIDATION_ERROR", response.getBody().getErrorCode());
        assertEquals("Validation failed for one or more fields", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
        assertEquals(2, response.getBody().getDetails().size());
        assertTrue(response.getBody().getDetails().contains("Title is required"));
        assertTrue(response.getBody().getDetails().contains("Description is required"));
    }

    @Test
    void handleMethodArgumentNotValid_WithSingleError_ReturnsBadRequest() throws Exception {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("taskCreateRequest", "title", "Title is required");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodParameter parameter = new MethodParameter(
            TaskController.class.getMethod("createTask", TaskCreateRequest.class), 0
        );
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMethodArgumentNotValid(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(1, response.getBody().getDetails().size());
    }

    // ========== GENERIC EXCEPTION TESTS ==========

    @Test
    void handleGenericException_ReturnsInternalServerError() {
        Exception exception = new Exception("Unexpected error occurred");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getErrorCode());
        assertEquals("An unexpected error occurred, please try again", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleGenericException_WithRuntimeException_ReturnsInternalServerError() {
        RuntimeException exception = new RuntimeException("Runtime error");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getErrorCode());
    }

    @Test
    void handleGenericException_WithNullPointerException_ReturnsInternalServerError() {
        NullPointerException exception = new NullPointerException("Null pointer error");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getErrorCode());
    }

    // ========== TIMESTAMP VALIDATION TESTS ==========

    @Test
    void allExceptionHandlers_SetTimestamp() {
        long beforeTimestamp = System.currentTimeMillis();

        ResponseEntity<ErrorResponse> response1 = exceptionHandler.handleDatabaseException(
            new DatabaseConnectionException("Test")
        );
        ResponseEntity<ErrorResponse> response2 = exceptionHandler.handleTimeoutException(
            new TimeoutException("Test")
        );
        ResponseEntity<ErrorResponse> response3 = exceptionHandler.handleTaskNotFoundException(
            new TaskNotFoundException("Test")
        );
        ResponseEntity<ErrorResponse> response4 = exceptionHandler.handleValidationException(
            new ValidationException("Test")
        );
        ResponseEntity<ErrorResponse> response5 = exceptionHandler.handleGenericException(
            new Exception("Test")
        );

        long afterTimestamp = System.currentTimeMillis();

        assertNotNull(response1.getBody().getTimestamp());
        assertNotNull(response2.getBody().getTimestamp());
        assertNotNull(response3.getBody().getTimestamp());
        assertNotNull(response4.getBody().getTimestamp());
        assertNotNull(response5.getBody().getTimestamp());

        assertTrue(response1.getBody().getTimestamp() >= beforeTimestamp);
        assertTrue(response1.getBody().getTimestamp() <= afterTimestamp);
    }
}
