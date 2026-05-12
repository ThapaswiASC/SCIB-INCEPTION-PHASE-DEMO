package com.myproject.models.dtos;

public class TaskCountResponse {

    private String userId;
    private Long taskCount;
    private Integer maxTasksAllowed;
    private Integer remainingCapacity;

    public TaskCountResponse() {
    }

    public TaskCountResponse(String userId, long taskCount) {
        this.userId = userId;
        this.taskCount = taskCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(Long taskCount) {
        this.taskCount = taskCount;
    }

    public Integer getMaxTasksAllowed() {
        return maxTasksAllowed;
    }

    public void setMaxTasksAllowed(Integer maxTasksAllowed) {
        this.maxTasksAllowed = maxTasksAllowed;
    }

    public Integer getRemainingCapacity() {
        return remainingCapacity;
    }

    public void setRemainingCapacity(Integer remainingCapacity) {
        this.remainingCapacity = remainingCapacity;
    }
}