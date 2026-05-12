package com.myproject.exceptions;

import com.myproject.models.dtos.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TaskLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleTaskLimitExceeded(TaskLimitExceededException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(System.currentTimeMillis());
        error.setTraceId(UUID.randomUUID().toString());
        error.setErrorCode("TASK_LIMIT_EXCEEDED");
        error.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConcurrentTaskCreationException.class)
    public ResponseEntity<ErrorResponse> handleConcurrentCreation(ConcurrentTaskCreationException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(System.currentTimeMillis());
        error.setTraceId(UUID.randomUUID().toString());
        error.setErrorCode("CONCURRENT_CREATION_ERROR");
        error.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(PerformanceThresholdExceededException.class)
    public ResponseEntity<ErrorResponse> handlePerformanceThreshold(PerformanceThresholdExceededException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(System.currentTimeMillis());
        error.setTraceId(UUID.randomUUID().toString());
        error.setErrorCode("PERFORMANCE_THRESHOLD_EXCEEDED");
        error.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFound(TaskNotFoundException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(System.currentTimeMillis());
        error.setTraceId(UUID.randomUUID().toString());
        error.setErrorCode("TASK_NOT_FOUND");
        error.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(System.currentTimeMillis());
        error.setTraceId(UUID.randomUUID().toString());
        error.setErrorCode("UNAUTHORIZED");
        error.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> details = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            details.add(error.getField() + ": " + error.getDefaultMessage());
        }
        
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(System.currentTimeMillis());
        error.setTraceId(UUID.randomUUID().toString());
        error.setErrorCode("VALIDATION_ERROR");
        error.setMessage("Validation failed");
        error.setDetails(details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(System.currentTimeMillis());
        error.setTraceId(UUID.randomUUID().toString());
        error.setErrorCode("INTERNAL_SERVER_ERROR");
        error.setMessage("An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}