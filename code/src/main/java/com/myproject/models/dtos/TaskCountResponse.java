package com.myproject.models.dtos;

public class TaskCountResponse {
    private Long userId;
    private Long taskCount;

    public TaskCountResponse() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(Long taskCount) {
        this.taskCount = taskCount;
    }
}