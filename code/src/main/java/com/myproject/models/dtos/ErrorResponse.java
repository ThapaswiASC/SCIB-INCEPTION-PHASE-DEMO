package com.myproject.models.dtos;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {

    private Integer status;
    private String code;
    private String message;
    private List<String> errors;
    private LocalDateTime timestamp;
    private String traceId;
    private List<String> details;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(Integer status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(Integer status, String code, String message, List<String> errors) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }
}