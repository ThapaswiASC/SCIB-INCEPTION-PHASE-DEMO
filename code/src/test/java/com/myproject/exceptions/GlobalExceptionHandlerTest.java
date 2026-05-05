package com.myproject.exceptions;

import com.myproject.models.dtos.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    // ========== TaskNotFoundException Tests ==========

    @Test
    void handleTaskNotFound_ReturnsNotFoundStatus() {
        // Arrange
        TaskNotFoundException exception = new TaskNotFoundException("123");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTaskNotFound(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("TASK_NOT_FOUND", response.getBody().getErrorCode());
        assertTrue(response.getBody().getMessage().contains("123"));
        assertNotNull(response.getBody().getTimestamp());
        assertNotNull(response.getBody().getTraceId());
        assertNotNull(response.getBody().getDetails());
    }

    @Test
    void handleTaskNotFound_WithLongId_ReturnsNotFoundStatus() {
        // Arrange
        TaskNotFoundException exception = new TaskNotFoundException(456L);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTaskNotFound(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("TASK_NOT_FOUND", response.getBody().getErrorCode());
        assertTrue(response.getBody().getMessage().contains("456"));
    }

    // ========== InvalidStatusTransitionException Tests ==========

    @Test
    void handleInvalidStatusTransition_ReturnsBadRequestStatus() {
        // Arrange
        InvalidStatusTransitionException exception = new InvalidStatusTransitionException("TO_DO", "DONE");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidStatusTransition(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_STATUS_TRANSITION", response.getBody().getErrorCode());
        assertTrue(response.getBody().getMessage().contains("TO_DO"));
        assertTrue(response.getBody().getMessage().contains("DONE"));
        assertNotNull(response.getBody().getTimestamp());
        assertNotNull(response.getBody().getTraceId());
    }

    // ========== ColumnNotFoundException Tests ==========

    @Test
    void handleColumnNotFound_ReturnsNotFoundStatus() {
        // Arrange
        ColumnNotFoundException exception = new ColumnNotFoundException("invalid-column");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleColumnNotFound(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("COLUMN_NOT_FOUND", response.getBody().getErrorCode());
        assertTrue(response.getBody().getMessage().contains("invalid-column"));
        assertNotNull(response.getBody().getTimestamp());
        assertNotNull(response.getBody().getTraceId());
    }

    // ========== TaskLimitExceededException Tests ==========

    @Test
    void handleTaskLimitExceeded_ReturnsBadRequestStatus() {
        // Arrange
        TaskLimitExceededException exception = new TaskLimitExceededException("User 1 has reached the maximum task limit of 10000");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTaskLimitExceeded(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("TASK_LIMIT_EXCEEDED", response.getBody().getErrorCode());
        assertTrue(response.getBody().getMessage().contains("10000"));
        assertNotNull(response.getBody().getTimestamp());
        assertNotNull(response.getBody().getTraceId());
    }

    // ========== ConcurrentTaskCreationException Tests ==========

    @Test
    void handleConcurrentCreation_ReturnsConflictStatus() {
        // Arrange
        ConcurrentTaskCreationException exception = new ConcurrentTaskCreationException("Concurrent modification detected");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleConcurrentCreation(exception);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CONCURRENT_CREATION_ERROR", response.getBody().getErrorCode());
        assertTrue(response.getBody().getMessage().contains("Concurrent modification"));
        assertNotNull(response.getBody().getTimestamp());
        assertNotNull(response.getBody().getTraceId());
    }

    // ========== PerformanceThresholdExceededException Tests ==========

    @Test
    void handlePerformanceThreshold_ReturnsServiceUnavailableStatus() {
        // Arrange
        PerformanceThresholdExceededException exception = new PerformanceThresholdExceededException("Service overloaded");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handlePerformanceThreshold(exception);

        // Assert
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("PERFORMANCE_THRESHOLD_EXCEEDED", response.getBody().getErrorCode());
        assertEquals("Service temporarily unavailable due to high load", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
        assertNotNull(response.getBody().getTraceId());
    }

    // ========== MethodArgumentNotValidException Tests ==========

    @Test
    void handleValidationException_ReturnsBadRequestStatus() {
        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("taskCreateRequest", "title", "Title is required");
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));
        
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALIDATION_ERROR", response.getBody().getErrorCode());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertNotNull(response.getBody().getDetails());
        assertFalse(response.getBody().getDetails().isEmpty());
        assertTrue(response.getBody().getDetails().get(0).contains("title"));
        assertTrue(response.getBody().getDetails().get(0).contains("Title is required"));
    }

    @Test
    void handleValidationException_MultipleErrors_ReturnsAllErrors() {
        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("taskCreateRequest", "title", "Title is required");
        FieldError fieldError2 = new FieldError("taskCreateRequest", "userId", "User ID must be positive");
        when(bindingResult.getFieldErrors()).thenReturn(java.util.Arrays.asList(fieldError1, fieldError2));
        
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getDetails().size());
    }

    // ========== Generic Exception Tests ==========

    @Test
    void handleGenericException_ReturnsInternalServerErrorStatus() {
        // Arrange
        Exception exception = new Exception("Unexpected error");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getErrorCode());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
        assertNotNull(response.getBody().getTraceId());
        assertNotNull(response.getBody().getDetails());
    }

    @Test
    void handleGenericException_RuntimeException_ReturnsInternalServerErrorStatus() {
        // Arrange
        RuntimeException exception = new RuntimeException("Runtime error");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getErrorCode());
    }

    // ========== TraceId Uniqueness Tests ==========

    @Test
    void exceptionHandlers_GenerateUniqueTraceIds() {
        // Arrange
        TaskNotFoundException exception1 = new TaskNotFoundException("1");
        TaskNotFoundException exception2 = new TaskNotFoundException("2");

        // Act
        ResponseEntity<ErrorResponse> response1 = exceptionHandler.handleTaskNotFound(exception1);
        ResponseEntity<ErrorResponse> response2 = exceptionHandler.handleTaskNotFound(exception2);

        // Assert
        assertNotEquals(response1.getBody().getTraceId(), response2.getBody().getTraceId());
    }
}