package com.myproject.services.interfaces;

import com.myproject.models.dtos.TaskCreateRequest;
import com.myproject.models.dtos.TaskResponse;
import com.myproject.models.dtos.TaskUpdateRequest;

public interface TaskService {
    TaskResponse createTask(TaskCreateRequest request);
    TaskResponse getTaskById(Long id);
    TaskResponse updateTask(Long id, TaskUpdateRequest request);
    void deleteTask(Long id);
}