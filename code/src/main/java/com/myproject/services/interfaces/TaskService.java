package com.myproject.services.interfaces;

import com.myproject.models.dtos.TaskCreateRequest;
import com.myproject.models.dtos.TaskResponse;
import com.myproject.models.dtos.TaskStatus;
import com.myproject.models.dtos.TaskUpdateRequest;

import java.util.List;

public interface TaskService {

    TaskResponse createTask(TaskCreateRequest request);

    TaskResponse getTaskById(Long id);

    TaskResponse updateTask(Long id, TaskUpdateRequest request);

    void deleteTask(Long id);

    List<TaskResponse> getTasksByStatus(TaskStatus status);

    List<TaskResponse> getAllTasks();
}
