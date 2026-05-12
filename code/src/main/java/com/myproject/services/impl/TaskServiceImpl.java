package com.myproject.services.impl;

import com.myproject.exceptions.PerformanceThresholdExceededException;
import com.myproject.exceptions.TaskLimitExceededException;
import com.myproject.exceptions.TaskNotFoundException;
import com.myproject.models.datastores.TaskDataStore;
import com.myproject.models.dtos.*;
import com.myproject.models.entities.Task;
import com.myproject.services.interfaces.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskDataStore taskDataStore;

    @Value("${task.user.limit:10000}")
    private int taskUserLimit;

    @Value("${task.creation.performance.threshold:200}")
    private long performanceThreshold;

    public TaskServiceImpl(TaskDataStore taskDataStore) {
        this.taskDataStore = taskDataStore;
    }

    @Override
    public TaskResponse createTask(TaskCreateRequest request) {
        long startTime = System.currentTimeMillis();

        // Validate task limit
        long currentTaskCount = taskDataStore.countByUserId(request.getUserId());
        if (currentTaskCount >= taskUserLimit) {
            throw new TaskLimitExceededException(
                String.format("User %d has reached the maximum limit of %d tasks", 
                    request.getUserId(), taskUserLimit)
            );
        }

        // Create task entity
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setUserId(request.getUserId());
        task.setPriority(request.getPriority());
        task.setStatus(TaskStatus.PENDING);
        task.setDueDate(request.getDueDate());
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        // Save task
        Task savedTask = taskDataStore.save(task);

        long duration = System.currentTimeMillis() - startTime;
        logger.info("Task created: userId={}, taskId={}, duration={}ms", 
            request.getUserId(), savedTask.getId(), duration);

        if (duration > performanceThreshold) {
            logger.warn("Performance threshold exceeded: duration={}ms", duration);
        }

        return mapToResponse(savedTask);
    }

    @Override
    public List<TaskResponse> getUserTasks(Long userId, int page, int size) {
        List<Task> tasks = taskDataStore.findByUserId(userId, page, size);
        return tasks.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public TaskCountResponse getTaskCount(Long userId) {
        long count = taskDataStore.countByUserId(userId);
        return new TaskCountResponse(userId, (int) count);
    }

    @Override
    public TaskResponse getTaskById(Long taskId) {
        Task task = taskDataStore.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException(
                String.format("Task with ID %d not found", taskId)
            ));
        return mapToResponse(task);
    }

    @Override
    public TaskResponse updateTask(Long taskId, TaskUpdateRequest request) {
        Task task = taskDataStore.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException(
                String.format("Task with ID %d not found", taskId)
            ));

        // Update fields if provided
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        task.setUpdatedAt(LocalDateTime.now());

        Task updatedTask = taskDataStore.save(task);
        logger.info("Task updated: taskId={}", taskId);

        return mapToResponse(updatedTask);
    }

    @Override
    public void deleteTask(Long taskId) {
        if (!taskDataStore.existsById(taskId)) {
            throw new TaskNotFoundException(
                String.format("Task with ID %d not found", taskId)
            );
        }
        taskDataStore.deleteById(taskId);
        logger.info("Task deleted: taskId={}", taskId);
    }

    @Override
    public BulkTaskResponse bulkCreateTasks(List<TaskCreateRequest> requests) {
        long startTime = System.currentTimeMillis();

        List<TaskResponse> successfulTasks = new ArrayList<>();
        List<ErrorResponse> errors = new ArrayList<>();

        for (int i = 0; i < requests.size(); i++) {
            try {
                TaskCreateRequest request = requests.get(i);
                TaskResponse response = createTask(request);
                successfulTasks.add(response);
            } catch (Exception e) {
                ErrorResponse error = new ErrorResponse(
                    "TASK_CREATION_FAILED",
                    String.format("Failed to create task at index %d: %s", i, e.getMessage())
                );
                errors.add(error);
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        logger.info("Bulk task creation completed: success={}, failures={}, duration={}ms",
            successfulTasks.size(), errors.size(), duration);

        if (duration > performanceThreshold * requests.size()) {
            logger.warn("Bulk operation performance threshold exceeded: duration={}ms", duration);
        }

        BulkTaskResponse response = new BulkTaskResponse();
        response.setSuccessCount(successfulTasks.size());
        response.setFailureCount(errors.size());
        response.setTasks(successfulTasks);
        response.setErrors(errors);

        return response;
    }

    private TaskResponse mapToResponse(Task task) {
        return new TaskResponse(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getUserId(),
            task.getPriority(),
            task.getStatus(),
            task.getCreatedAt(),
            task.getUpdatedAt(),
            task.getDueDate()
        );
    }
}