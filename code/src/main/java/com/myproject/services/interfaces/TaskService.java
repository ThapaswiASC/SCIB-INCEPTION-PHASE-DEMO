package com.myproject.services.interfaces;

import com.myproject.models.dtos.*;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    TaskResponse createTask(String userId, TaskCreateRequest request);
    BulkTaskResponse bulkCreateTasks(String userId, List<TaskCreateRequest> requests);
    TaskResponse getTaskById(UUID taskId, String userId);
    TaskResponse updateTask(UUID taskId, String userId, TaskUpdateRequest request);
    void deleteTask(UUID taskId, String userId);
    List<TaskResponse> getUserTasks(String userId, int page, int size, String sort);
    TaskCountResponse getTaskCount(String userId);
}