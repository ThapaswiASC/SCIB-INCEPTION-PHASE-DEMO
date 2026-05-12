package com.myproject.models.entities;

import java.time.LocalDateTime;

public class TaskCounter {

    private String userId;
    private Long taskCount;
    private LocalDateTime lastUpdated;
    private Long version;

    public TaskCounter() {
        this.taskCount = 0L;
        this.lastUpdated = LocalDateTime.now();
        this.version = 0L;
    }

    public TaskCounter(String userId) {
        this();
        this.userId = userId;
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

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public void incrementCount() {
        this.taskCount++;
        this.lastUpdated = LocalDateTime.now();
        this.version++;
    }

    public void decrementCount() {
        if (this.taskCount > 0) {
            this.taskCount--;
            this.lastUpdated = LocalDateTime.now();
            this.version++;
        }
    }
}