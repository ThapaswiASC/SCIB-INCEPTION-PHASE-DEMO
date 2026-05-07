package com.myproject.models.dtos;

public enum TaskStatus {
    TO_DO,
    IN_PROGRESS,
    DONE,
    PENDING,
    COMPLETED,
    CANCELLED;
    
    public String name() {
        return super.name();
    }
}