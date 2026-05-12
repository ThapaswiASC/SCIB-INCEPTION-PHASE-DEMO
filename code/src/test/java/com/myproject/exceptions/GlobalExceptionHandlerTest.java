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

    // ========== TaskLimitExceededException Tests ==========

    @Test
    void handleTaskLimitExceeded_ReturnsCorrectErrorResponse() {
        // Arrange
        TaskLimitExceededException exception = new TaskLimitExceededException(
            "User 1 has reached the maximum limit of 10000 tasks"
        );

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTaskLimitExceeded(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("TASK_LIMIT_EXCEEDED", response.getBody().getCode());
        assertEquals("User 1 has reached the maximum limit of 10000 tasks", response.getBody().getMessage());
        assertNotNull(response.getBody().getTraceId());
        assertEquals(1, response.getBody().getDetails().size());
        assertEquals("User has reached the maximum limit of 10,000 tasks", response.getBody().getDetails().get(0));
    }

    // ========== ConcurrentTaskCreationException Tests ==========

    @Test
    void handleConcurrentCreation_ReturnsCorrectErrorResponse() {
        // Arrange
        ConcurrentTaskCreationException exception = new ConcurrentTaskCreationException(
            "Concurrent task creation detected for user 1"
        );

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleConcurrentCreation(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CONCURRENT_CREATION_ERROR", response.getBody().getCode());
        assertEquals("Concurrent task creation detected for user 1", response.getBody().getMessage());
        assertNotNull(response.getBody().getTraceId());
        assertEquals(1, response.getBody().getDetails().size());
        assertEquals("Concurrent task creation detected, please retry", response.getBody().getDetails().get(0));
    }

    // ========== PerformanceThresholdExceededException Tests ==========

    @Test
    void handlePerformanceThreshold_ReturnsCorrectErrorResponse() {
        // Arrange
        PerformanceThresholdExceededException exception = new PerformanceThresholdExceededException(
            "Task creation took 350ms, exceeding threshold of 200ms"
        );

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handlePerformanceThreshold(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("PERFORMANCE_THRESHOLD_EXCEEDED", response.getBody().getCode());
        assertEquals("Task creation took 350ms, exceeding threshold of 200ms", response.getBody().getMessage());
        assertNotNull(response.getBody().getTraceId());
        assertEquals(1, response.getBody().getDetails().size());
        assertEquals("System performance threshold exceeded, please try again later", response.getBody().getDetails().get(0));
    }

    // ========== TaskNotFoundException Tests ==========

    @Test
    void handleTaskNotFound_ReturnsCorrectErrorResponse() {
        // Arrange
        TaskNotFoundException exception = new TaskNotFoundException(
            "Task with ID 999 not found"
        );

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTaskNotFound(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("TASK_NOT_FOUND", response.getBody().getCode());
        assertEquals("Task with ID 999 not found", response.getBody().getMessage());
        assertNotNull(response.getBody().getTraceId());
        assertEquals(1, response.getBody().getDetails().size());
        assertEquals("The requested task does not exist", response.getBody().getDetails().get(0));
    }

    // ========== MethodArgumentNotValidException Tests ==========

    @Test
    void handleValidationErrors_ReturnsCorrectErrorResponse() {
        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("taskCreateRequest", "title", "Task title is required");
        FieldError fieldError2 = new FieldError("taskCreateRequest", "userId", "Valid user ID is required");
        List<FieldError> fieldErrors = Arrays.asList(fieldError1, fieldError2);

        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
            null,
            bindingResult
        );

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationErrors(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALIDATION_ERROR", response.getBody().getCode());
        assertEquals("Validation failed for request", response.getBody().getMessage());
        assertNotNull(response.getBody().getTraceId());
        assertEquals(2, response.getBody().getDetails().size());
        assertTrue(response.getBody().getDetails().contains("title: Task title is required"));
        assertTrue(response.getBody().getDetails().contains("userId: Valid user ID is required"));
    }

    @Test
    void handleValidationErrors_SingleFieldError_ReturnsCorrectErrorResponse() {
        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("taskCreateRequest", "priority", "Valid priority level is required");
        List<FieldError> fieldErrors = Arrays.asList(fieldError);

        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
            null,
            bindingResult
        );

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationErrors(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getDetails().size());
        assertEquals("priority: Valid priority level is required", response.getBody().getDetails().get(0));
    }

    // ========== Generic Exception Tests ==========

    @Test
    void handleGenericException_ReturnsCorrectErrorResponse() {
        // Arrange
        Exception exception = new RuntimeException("Unexpected error occurred");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getCode());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
        assertNotNull(response.getBody().getTraceId());
        assertEquals(1, response.getBody().getDetails().size());
        assertEquals("Unexpected error occurred", response.getBody().getDetails().get(0));
    }

    @Test
    void handleGenericException_NullPointerException_ReturnsCorrectErrorResponse() {
        // Arrange
        Exception exception = new NullPointerException("Null value encountered");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getCode());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
        assertNotNull(response.getBody().getTraceId());
        assertEquals(1, response.getBody().getDetails().size());
        assertEquals("Null value encountered", response.getBody().getDetails().get(0));
    }

    // ========== TraceId Generation Tests ==========

    @Test
    void allHandlers_GenerateUniqueTraceIds() {
        // Arrange
        TaskLimitExceededException ex1 = new TaskLimitExceededException("Error 1");
        TaskNotFoundException ex2 = new TaskNotFoundException("Error 2");

        // Act
        ResponseEntity<ErrorResponse> response1 = exceptionHandler.handleTaskLimitExceeded(ex1);
        ResponseEntity<ErrorResponse> response2 = exceptionHandler.handleTaskNotFound(ex2);

        // Assert
        assertNotNull(response1.getBody().getTraceId());
        assertNotNull(response2.getBody().getTraceId());
        assertNotEquals(response1.getBody().getTraceId(), response2.getBody().getTraceId());
    }

    // ========== Timestamp Tests ==========

    @Test
    void allHandlers_SetTimestamp() {
        // Arrange
        TaskLimitExceededException exception = new TaskLimitExceededException("Test error");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTaskLimitExceeded(exception);

        // Assert
        assertNotNull(response.getBody().getTimestamp());
    }
}