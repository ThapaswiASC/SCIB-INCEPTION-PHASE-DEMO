package com.myproject.models.dtos;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {
    private LocalDateTime timestamp;
    private String traceId;
    private String errorCode;
    private String message;
    private List<String> details;

    public ErrorResponse() {
    }

    private ErrorResponse(Builder builder) {
        this.timestamp = builder.timestamp;
        this.traceId = builder.traceId;
        this.errorCode = builder.errorCode;
        this.message = builder.message;
        this.details = builder.details;
    }

    public static Builder builder() {
        return new Builder();
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

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    public static class Builder {
        private LocalDateTime timestamp;
        private String traceId;
        private String errorCode;
        private String message;
        private List<String> details;

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder details(List<String> details) {
            this.details = details;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(this);
        }
    }
}
