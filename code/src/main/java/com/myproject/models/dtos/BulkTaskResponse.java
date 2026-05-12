package com.myproject.models.dtos;

import java.util.ArrayList;
import java.util.List;

public class BulkTaskResponse {

    private Integer totalCreated;
    private List<TaskResponse> tasks = new ArrayList<>();
    private List<BulkTaskError> errors = new ArrayList<>();

    public BulkTaskResponse() {
    }

    public BulkTaskResponse(int successCount, int failureCount, List<TaskResponse> tasks, List<BulkTaskError> errors) {
        this.totalCreated = successCount;
        this.tasks = tasks;
        this.errors = errors;
    }

    public Integer getTotalCreated() {
        return totalCreated;
    }

    public void setTotalCreated(Integer totalCreated) {
        this.totalCreated = totalCreated;
    }

    public List<TaskResponse> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskResponse> tasks) {
        this.tasks = tasks;
    }

    public List<BulkTaskError> getErrors() {
        return errors;
    }

    public void setErrors(List<BulkTaskError> errors) {
        this.errors = errors;
    }

    public int getSuccessCount() {
        return totalCreated != null ? totalCreated : 0;
    }

    public int getFailureCount() {
        return errors != null ? errors.size() : 0;
    }

    public static class BulkTaskError {
        private Integer index;
        private String errorCode;
        private String message;

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
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
    }
}