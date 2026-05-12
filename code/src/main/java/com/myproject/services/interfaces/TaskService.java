package com.myproject.services.interfaces;

import com.myproject.models.dtos.*;
import java.util.List;

public interface TaskService {

    TaskResponse createTask(TaskCreateRequest request);

    List<TaskResponse> getUserTasks(Long userId, int page, int size);

    TaskCountResponse getTaskCount(Long userId);

    TaskResponse getTaskById(Long taskId);

    TaskResponse updateTask(Long taskId, TaskUpdateRequest request);

    void deleteTask(Long taskId);

    BulkTaskResponse bulkCreateTasks(List<TaskCreateRequest> requests);
}