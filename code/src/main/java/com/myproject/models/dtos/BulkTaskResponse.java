package com.myproject.models.dtos;

import java.util.List;

public class BulkTaskResponse {

    private List<TaskResponse> createdTasks;
    private Integer totalCreated;
    private List<FailureInfo> failures;

    public BulkTaskResponse() {
    }

    public List<TaskResponse> getCreatedTasks() {
        return createdTasks;
    }

    public void setCreatedTasks(List<TaskResponse> createdTasks) {
        this.createdTasks = createdTasks;
    }

    public Integer getTotalCreated() {
        return totalCreated;
    }

    public void setTotalCreated(Integer totalCreated) {
        this.totalCreated = totalCreated;
    }

    public List<FailureInfo> getFailures() {
        return failures;
    }

    public void setFailures(List<FailureInfo> failures) {
        this.failures = failures;
    }

    public static class FailureInfo {
        private Integer index;
        private String error;

        public FailureInfo() {
        }

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