package com.myproject.exceptions;

import com.myproject.models.dtos.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFound(TaskNotFoundException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setTraceId(UUID.randomUUID().toString());
        error.setErrorCode("TASK_NOT_FOUND");
        error.setMessage(ex.getMessage());
        error.setDetails(new ArrayList<>());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidStatusTransition(InvalidStatusTransitionException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setTraceId(UUID.randomUUID().toString());
        error.setErrorCode("INVALID_STATUS_TRANSITION");
        error.setMessage(ex.getMessage());
        error.setDetails(new ArrayList<>());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ColumnNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleColumnNotFound(ColumnNotFoundException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setTraceId(UUID.randomUUID().toString());
        error.setErrorCode("COLUMN_NOT_FOUND");
        error.setMessage(ex.getMessage());
        error.setDetails(new ArrayList<>());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(TaskLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleTaskLimitExceeded(TaskLimitExceededException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setTraceId(UUID.randomUUID().toString());
        error.setErrorCode("TASK_LIMIT_EXCEEDED");
        error.setMessage(ex.getMessage());
        error.setDetails(new ArrayList<>());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConcurrentTaskCreationException.class)
    public ResponseEntity<ErrorResponse> handleConcurrentCreation(ConcurrentTaskCreationException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setTraceId(UUID.randomUUID().toString());
        error.setErrorCode("CONCURRENT_CREATION_ERROR");
        error.setMessage(ex.getMessage());
        error.setDetails(new ArrayList<>());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(PerformanceThresholdExceededException.class)
    public ResponseEntity<ErrorResponse> handlePerformanceThreshold(PerformanceThresholdExceededException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setTraceId(UUID.randomUUID().toString());
        error.setErrorCode("PERFORMANCE_THRESHOLD_EXCEEDED");
        error.setMessage("Service temporarily unavailable due to high load");
        error.setDetails(new ArrayList<>());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setTraceId(UUID.randomUUID().toString());
        error.setErrorCode("VALIDATION_ERROR");
        error.setMessage("Validation failed");
        List<String> details = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> 
            details.add(fieldError.getField() + ": " + fieldError.getDefaultMessage())
        );
        error.setDetails(details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setTraceId(UUID.randomUUID().toString());
        error.setErrorCode("INTERNAL_SERVER_ERROR");
        error.setMessage("An unexpected error occurred");
        error.setDetails(new ArrayList<>());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}