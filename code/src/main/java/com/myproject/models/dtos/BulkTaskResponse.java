package com.myproject.models.dtos;

import java.util.List;

public class BulkTaskResponse {
    private Integer successCount;
    private Integer failureCount;
    private List<TaskResponse> createdTasks;
    private List<BulkError> errors;

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(Integer failureCount) {
        this.failureCount = failureCount;
    }

    public List<TaskResponse> getCreatedTasks() {
        return createdTasks;
    }

    public void setCreatedTasks(List<TaskResponse> createdTasks) {
        this.createdTasks = createdTasks;
    }

    public List<BulkError> getErrors() {
        return errors;
    }

    public void setErrors(List<BulkError> errors) {
        this.errors = errors;
    }

    public static class BulkError {
        private Integer index;
        private String message;

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
