package com.myproject.services.impl;

import com.myproject.exceptions.TaskLimitExceededException;
import com.myproject.exceptions.TaskNotFoundException;
import com.myproject.models.datastores.TaskDataStore;
import com.myproject.models.dtos.*;
import com.myproject.models.entities.Task;
import com.myproject.services.interfaces.ColumnService;
import com.myproject.services.interfaces.TaskService;
import com.myproject.services.interfaces.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private static final int MAX_TASKS_PER_USER = 10000;

    @Autowired
    private TaskDataStore taskDataStore;

    @Autowired
    private ColumnService columnService;

    @Autowired
    private ValidationService validationService;

    @Override
    public TaskResponse createTask(TaskCreateRequest request) {
        validationService.validateTaskInput(request);
        validationService.validateTaskLimit(request.getUserId());

        Long currentCount = taskDataStore.countByUserId(request.getUserId());
        if (currentCount >= MAX_TASKS_PER_USER) {
            throw new TaskLimitExceededException("User has reached the maximum task limit of " + MAX_TASKS_PER_USER);
        }

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setUserId(request.getUserId());
        task.setStatus("PENDING");
        task.setPriority(request.getPriority() != null ? request.getPriority().name() : "MEDIUM");
        task.setDueDate(request.getDueDate());

        Task savedTask = taskDataStore.save(task);
        return mapToTaskResponse(savedTask);
    }

    @Override
    public TaskResponse getTaskById(Long taskId) {
        Task task = taskDataStore.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        return mapToTaskResponse(task);
    }

    @Override
    public PagedTaskResponse getUserTasks(Long userId, int page, int size) {
        List<Task> tasks = taskDataStore.findByUserId(userId, page, size);
        Long totalElements = taskDataStore.countByUserId(userId);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        List<TaskResponse> taskResponses = new ArrayList<>();
        for (Task task : tasks) {
            taskResponses.add(mapToTaskResponse(task));
        }

        return new PagedTaskResponse(taskResponses, totalElements, totalPages, size, page);
    }

    @Override
    public TaskCountResponse getTaskCount(Long userId) {
        Long count = taskDataStore.countByUserId(userId);
        return new TaskCountResponse(userId, count);
    }

    @Override
    public TaskResponse updateTask(Long taskId, TaskUpdateRequest request) {
        Task task = taskDataStore.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        if (request.getTitle() != null) {
            validationService.validateTitle(request.getTitle());
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus().name());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority().name());
        }

        task.setUpdatedAt(LocalDateTime.now());
        Task updatedTask = taskDataStore.save(task);
        return mapToTaskResponse(updatedTask);
    }

    @Override
    public void deleteTask(Long taskId) {
        if (!taskDataStore.findById(taskId).isPresent()) {
            throw new TaskNotFoundException(taskId);
        }
        taskDataStore.deleteById(taskId);
    }

    @Override
    public BulkTaskResponse bulkCreateTasks(List<TaskCreateRequest> requests) {
        List<TaskResponse> createdTasks = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        for (TaskCreateRequest request : requests) {
            try {
                TaskResponse response = createTask(request);
                createdTasks.add(response);
                successCount++;
            } catch (Exception e) {
                errors.add("Failed to create task: " + e.getMessage());
                failureCount++;
            }
        }

        return new BulkTaskResponse(createdTasks, successCount, failureCount, errors);
    }

    @Override
    public UpdateTaskStatusResponse updateTaskStatus(String taskId, UpdateTaskStatusRequest request) {
        Long id = Long.parseLong(taskId);
        Task task = taskDataStore.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        String oldStatus = task.getStatus();
        String oldColumnId = task.getColumnId();
        String newStatus = request.getStatus().name();

        validationService.validateStatusTransition(oldStatus, newStatus);

        task.setStatus(newStatus);
        task.setColumnId(request.getColumnId());
        task.setUpdatedAt(LocalDateTime.now());

        taskDataStore.save(task);

        if (oldColumnId != null && !oldColumnId.equals(request.getColumnId())) {
            columnService.updateColumnCounts(oldColumnId, request.getColumnId());
        }

        return new UpdateTaskStatusResponse(taskId, newStatus, task.getUpdatedAt());
    }

    @Override
    public TaskDetailsResponse getTaskDetails(String taskId) {
        Long id = Long.parseLong(taskId);
        Task task = taskDataStore.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        return new TaskDetailsResponse(taskId, task.getTitle(), task.getStatus(), task.getColumnId());
    }

    private TaskResponse mapToTaskResponse(Task task) {
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
