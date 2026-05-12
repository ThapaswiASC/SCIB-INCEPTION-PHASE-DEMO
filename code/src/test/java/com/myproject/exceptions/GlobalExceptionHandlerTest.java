package com.myproject.exceptions;

import com.myproject.models.dtos.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleTaskLimitExceeded_ReturnsCorrectErrorResponse() {
        TaskLimitExceededException exception = new TaskLimitExceededException(
            "User has reached maximum task limit of 10000"
        );

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTaskLimitExceeded(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("TASK_LIMIT_EXCEEDED", response.getBody().getCode());
        assertEquals("User has reached maximum task limit of 10000", response.getBody().getMessage());
        assertNotNull(response.getBody().getTraceId());
    }

    @Test
    void handleConcurrentCreation_ReturnsCorrectErrorResponse() {
        ConcurrentTaskCreationException exception = new ConcurrentTaskCreationException(
            "Concurrent task creation detected"
        );

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleConcurrentCreation(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CONCURRENT_CREATION_ERROR", response.getBody().getCode());
        assertNotNull(response.getBody().getTraceId());
    }

    @Test
    void handlePerformanceThreshold_ReturnsCorrectErrorResponse() {
        PerformanceThresholdExceededException exception = new PerformanceThresholdExceededException(
            "Task creation exceeded threshold"
        );

        ResponseEntity<ErrorResponse> response = exceptionHandler.handlePerformanceThreshold(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("PERFORMANCE_THRESHOLD_EXCEEDED", response.getBody().getCode());
        assertNotNull(response.getBody().getTraceId());
    }

    @Test
    void handleTaskNotFound_ReturnsCorrectErrorResponse() {
        TaskNotFoundException exception = new TaskNotFoundException(
            "Task not found"
        );

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTaskNotFound(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("TASK_NOT_FOUND", response.getBody().getCode());
        assertNotNull(response.getBody().getTraceId());
    }

    @Test
    void handleUnauthorized_ReturnsCorrectErrorResponse() {
        UnauthorizedException exception = new UnauthorizedException(
            "Access denied"
        );

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUnauthorized(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UNAUTHORIZED", response.getBody().getCode());
        assertNotNull(response.getBody().getTraceId());
    }

    @Test
    void handleValidationExceptions_ReturnsCorrectErrorResponse() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("taskCreateRequest", "title", "Title is required");
        FieldError fieldError2 = new FieldError("taskCreateRequest", "priority", "Priority is required");
        List<FieldError> fieldErrors = Arrays.asList(fieldError1, fieldError2);

        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
            null,
            bindingResult
        );

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationExceptions(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALIDATION_ERROR", response.getBody().getCode());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertNotNull(response.getBody().getTraceId());
        assertEquals(2, response.getBody().getDetails().size());
    }

    @Test
    void handleGenericException_ReturnsCorrectErrorResponse() {
        Exception exception = new RuntimeException("Unexpected error");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getCode());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
        assertNotNull(response.getBody().getTraceId());
    }
}