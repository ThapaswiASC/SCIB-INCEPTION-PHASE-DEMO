package com.myproject.services.impl;

import com.myproject.exceptions.*;
import com.myproject.models.datastores.TaskDataStore;
import com.myproject.models.dtos.*;
import com.myproject.models.entities.Task;
import com.myproject.services.interfaces.TaskService;
import com.myproject.services.interfaces.ColumnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);
    private static final Long MAX_TASKS_PER_USER = 10000L;

    @Autowired
    private TaskDataStore taskDataStore;

    @Autowired
    private ColumnService columnService;

    @Override
    public TaskResponse createTask(TaskCreateRequest request) {
        logger.debug("Creating task for user: {}", request.getUserId());
        
        validateTaskInput(request);
        
        Long currentTaskCount = taskDataStore.countByUserId(request.getUserId());
        if (currentTaskCount >= MAX_TASKS_PER_USER) {
            throw new TaskLimitExceededException(request.getUserId(), currentTaskCount, MAX_TASKS_PER_USER);
        }
        
        Task task = new Task();
        task.setTitle(request.getTitle().trim());
        task.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        task.setUserId(request.getUserId());
        task.setPriority(request.getPriority() != null ? request.getPriority() : "MEDIUM");
        task.setStatus("TODO");
        task.setColumnId("to-do");
        
        Task savedTask = taskDataStore.save(task);
        logger.info("Task created successfully with ID: {}", savedTask.getId());
        
        return mapToResponse(savedTask);
    }

    @Override
    public TaskResponse updateTask(Long taskId, TaskUpdateRequest request) {
        logger.debug("Updating task: {}", taskId);
        
        Task task = taskDataStore.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException(taskId));
        
        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            if (request.getTitle().trim().length() > 255) {
                throw new InvalidInputException("Title exceeds maximum character limit of 255");
            }
            task.setTitle(request.getTitle().trim());
        }
        
        if (request.getDescription() != null) {
            if (request.getDescription().length() > 10000) {
                throw new InvalidInputException("Description exceeds maximum character limit of 10000");
            }
            task.setDescription(request.getDescription().trim());
        }
        
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        
        task.setUpdatedAt(LocalDateTime.now());
        Task updatedTask = taskDataStore.save(task);
        
        logger.info("Task updated successfully: {}", taskId);
        return mapToResponse(updatedTask);
    }

    @Override
    public TaskResponse getTaskById(Long taskId) {
        logger.debug("Fetching task: {}", taskId);
        Task task = taskDataStore.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException(taskId));
        return mapToResponse(task);
    }

    @Override
    public List<TaskResponse> listTasks() {
        logger.debug("Listing all tasks");
        return taskDataStore.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteTask(Long taskId) {
        logger.debug("Deleting task: {}", taskId);
        if (!taskDataStore.existsById(taskId)) {
            throw new TaskNotFoundException(taskId);
        }
        taskDataStore.deleteById(taskId);
        logger.info("Task deleted successfully: {}", taskId);
    }

    @Override
    public PagedTaskResponse getUserTasks(Long userId, int page, int size) {
        logger.debug("Fetching tasks for user: {}, page: {}, size: {}", userId, page, size);
        
        List<Task> tasks = taskDataStore.findByUserIdPaginated(userId, page, size);
        Long totalElements = taskDataStore.countByUserId(userId);
        int totalPages = (int) Math.ceil((double) totalElements / size);
        
        List<TaskResponse> content = tasks.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
        
        return new PagedTaskResponse(content, totalElements, totalPages, page, size);
    }

    @Override
    public TaskCountResponse getTaskCount(Long userId) {
        logger.debug("Getting task count for user: {}", userId);
        Long count = taskDataStore.countByUserId(userId);
        return new TaskCountResponse(userId, count);
    }

    @Override
    public BulkTaskResponse bulkCreateTasks(List<TaskCreateRequest> requests) {
        logger.debug("Bulk creating {} tasks", requests.size());
        
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
        
        logger.info("Bulk task creation completed. Success: {}, Failures: {}", successCount, failureCount);
        return new BulkTaskResponse(successCount, failureCount, createdTasks, errors);
    }

    @Override
    public UpdateTaskStatusResponse updateTaskStatus(String taskId, UpdateTaskStatusRequest request) {
        logger.debug("Updating task status: {}", taskId);
        
        Long id = Long.parseLong(taskId);
        Task task = taskDataStore.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
        
        String oldStatus = task.getStatus();
        String oldColumnId = task.getColumnId();
        
        validateStatusTransition(oldStatus, request.getStatus());
        
        task.setStatus(request.getStatus());
        task.setColumnId(request.getColumnId());
        task.setUpdatedAt(LocalDateTime.now());
        
        taskDataStore.save(task);
        
        if (oldColumnId != null && !oldColumnId.equals(request.getColumnId())) {
            columnService.updateColumnCounts(oldColumnId, request.getColumnId());
        }
        
        logger.info("Task status updated: {} from {} to {}", taskId, oldStatus, request.getStatus());
        return new UpdateTaskStatusResponse(taskId, request.getStatus(), LocalDateTime.now());
    }

    @Override
    public TaskDetailsResponse getTaskDetails(String taskId) {
        logger.debug("Fetching task details: {}", taskId);
        
        Long id = Long.parseLong(taskId);
        Task task = taskDataStore.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
        
        return new TaskDetailsResponse(
            taskId,
            task.getTitle(),
            task.getStatus(),
            task.getColumnId()
        );
    }

    private void validateTaskInput(TaskCreateRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new InvalidInputException("Title is required and cannot be empty or contain only whitespace");
        }
        
        if (request.getTitle().trim().length() > 255) {
            throw new InvalidInputException("Title exceeds maximum character limit of 255");
        }
        
        if (request.getDescription() != null && request.getDescription().length() > 2000) {
            throw new InvalidInputException("Description exceeds maximum character limit of 2000");
        }
    }

    private void validateStatusTransition(String fromStatus, String toStatus) {
        if (fromStatus == null || toStatus == null) {
            return;
        }
        
        if (fromStatus.equals("DONE") && !toStatus.equals("DONE")) {
            throw new InvalidStatusTransitionException(fromStatus, toStatus);
        }
    }

    private TaskResponse mapToResponse(Task task) {
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