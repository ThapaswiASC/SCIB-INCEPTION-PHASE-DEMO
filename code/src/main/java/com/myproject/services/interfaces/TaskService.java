package com.myproject.services.interfaces;

import com.myproject.models.dtos.*;

import java.util.List;

public interface TaskService {

    TaskStatusUpdateResponse updateTaskStatus(Long taskId, TaskStatusUpdateRequest request);

    TaskDetailsResponse getTaskById(Long taskId);

    List<TaskSummary> getTasksByStatus(String status);

    ValidateTaskMoveResponse validateTaskMove(Long taskId, ValidateTaskMoveRequest request);

    TaskResponse createTask(TaskCreateRequest request);

    TaskResponse updateTask(Long taskId, TaskUpdateRequest request);

    void deleteTask(Long taskId);

    PagedTaskResponse getUserTasks(Long userId, int page, int size);

    TaskCountResponse getTaskCount(Long userId);

    BulkTaskResponse bulkCreateTasks(List<TaskCreateRequest> requests);
}