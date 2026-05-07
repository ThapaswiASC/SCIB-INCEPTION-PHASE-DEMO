package com.myproject.models.dtos;

import java.util.List;

public class BulkTaskResponse {

    private Integer successCount;
    private Integer failureCount;
    private List<TaskResponse> createdTasks;
    private List<BulkError> errors;

    // Constructors
    public BulkTaskResponse() {}

    public BulkTaskResponse(Integer successCount, Integer failureCount, 
                           List<TaskResponse> createdTasks, List<BulkError> errors) {
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.createdTasks = createdTasks;
        this.errors = errors;
    }

    // Getters and Setters
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
        private String error;

        // Constructors
        public BulkError() {}

        public BulkError(Integer index, String error) {
            this.index = index;
            this.error = error;
        }

        // Getters and Setters
        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}