package com.myproject.utils;

import com.myproject.models.dtos.TaskCreateRequest;
import com.myproject.models.dtos.TaskResponse;
import com.myproject.models.entities.Task;

public class TaskMapper {

    private TaskMapper() {
        // Utility class
    }

    public static Task toEntity(TaskCreateRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setUserId(request.getUserId());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setStatus("PENDING");
        return task;
    }

    public static TaskResponse toResponse(Task task) {
        return new TaskResponse(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getUserId(),
            task.getStatus(),
            task.getPriority(),
            task.getCreatedAt(),
            task.getUpdatedAt()
        );
    }
}