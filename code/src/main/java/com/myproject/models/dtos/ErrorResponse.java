package com.myproject.models.dtos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ErrorResponse {

    private String code;
    private String message;
    private LocalDateTime timestamp;
    private Object preservedInput;
    private List<String> details;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
        this.details = new ArrayList<>();
    }

    public ErrorResponse(String code, String message) {
        this();
        this.code = code;
        this.message = message;
    }

    public ErrorResponse(String code, String message, Object preservedInput) {
        this(code, message);
        this.preservedInput = preservedInput;
    }

    public static Builder builder() {
        return new Builder();
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Object getPreservedInput() {
        return preservedInput;
    }

    public void setPreservedInput(Object preservedInput) {
        this.preservedInput = preservedInput;
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

    public static class Builder {
        private String code;
        private String message;
        private Object preservedInput;
        private LocalDateTime timestamp;
        private List<String> details;

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder preservedInput(Object preservedInput) {
            this.preservedInput = preservedInput;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder details(List<String> details) {
            this.details = details;
            return this;
        }

        public ErrorResponse build() {
            ErrorResponse response = new ErrorResponse();
            response.setCode(this.code);
            response.setMessage(this.message);
            response.setPreservedInput(this.preservedInput);
            response.setTimestamp(this.timestamp != null ? this.timestamp : LocalDateTime.now());
            response.setDetails(this.details != null ? this.details : new ArrayList<>());
            return response;
        }
    }
}