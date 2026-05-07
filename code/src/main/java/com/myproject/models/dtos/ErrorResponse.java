package com.myproject.models.dtos;

import java.util.ArrayList;
import java.util.List;

public class ErrorResponse {

    private String errorCode;
    private String message;
    private Long timestamp;
    private List<String> details;

    public ErrorResponse() {
        this.details = new ArrayList<>();
    }

    public ErrorResponse(String errorCode, String message, Long timestamp) {
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = timestamp;
        this.details = new ArrayList<>();
    }

    public ErrorResponse(String errorCode, String message, Long timestamp, List<String> details) {
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = timestamp;
        this.details = details != null ? details : new ArrayList<>();
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    public void addDetail(String detail) {
        if (this.details == null) {
            this.details = new ArrayList<>();
        }
        this.details.add(detail);
    }
}
