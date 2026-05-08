package com.myproject.services.impl;

import com.myproject.exceptions.InvalidStatusTransitionException;
import com.myproject.exceptions.TaskLimitExceededException;
import com.myproject.exceptions.TaskNotFoundException;
import com.myproject.models.datastores.TaskDataStore;
import com.myproject.models.dtos.*;
import com.myproject.models.entities.Task;
import com.myproject.services.interfaces.TaskService;
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
    private static final int MAX_TASKS_PER_USER = 10000;

    @Autowired
    private TaskDataStore taskDataStore;

    @Override
    public TaskStatusUpdateResponse updateTaskStatus(Long taskId, TaskStatusUpdateRequest request) {
        Task task = taskDataStore.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        String fromStatus = task.getStatus();
        String toStatus = request.getStatus().name();

        if (!isValidTransition(fromStatus, toStatus)) {
            throw new InvalidStatusTransitionException(fromStatus, toStatus);
        }

        task.setStatus(toStatus);
        task.setColumnId(request.getColumnId());
        task.setUpdatedAt(LocalDateTime.now());
        taskDataStore.save(task);

        logger.info("Task {} status updated from {} to {}", taskId, fromStatus, toStatus);

        return TaskStatusUpdateResponse.builder()
                .taskId(task.getId())
                .status(task.getStatus())
                .updatedAt(task.getUpdatedAt())
                .message("Task status updated successfully")
                .build();
    }

    @Override
    public TaskDetailsResponse getTaskById(Long taskId) {
        Task task = taskDataStore.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        return TaskDetailsResponse.builder()
                .taskId(task.getId())
                .title(task.getTitle())
                .status(task.getStatus())
                .assignee(task.getAssignee())
                .createdAt(task.getCreatedAt())
                .build();
    }

    @Override
    public List<TaskSummary> getTasksByStatus(String status) {
        return taskDataStore.findByStatus(status).stream()
                .map(task -> TaskSummary.builder()
                        .taskId(task.getId())
                        .title(task.getTitle())
                        .status(task.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public ValidateTaskMoveResponse validateTaskMove(Long taskId, ValidateTaskMoveRequest request) {
        taskDataStore.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        String fromStatus = request.getFromStatus().name();
        String toStatus = request.getToStatus().name();
        boolean valid = isValidTransition(fromStatus, toStatus);

        String message = valid
                ? "Move operation is valid"
                : String.format("Cannot transition from %s to %s", fromStatus, toStatus);

        return ValidateTaskMoveResponse.builder()
                .valid(valid)
                .message(message)
                .build();
    }

    @Override
    public TaskResponse createTask(TaskCreateRequest request) {
        Long currentCount = taskDataStore.countByUserId(request.getUserId());
        if (currentCount >= MAX_TASKS_PER_USER) {
            throw new TaskLimitExceededException(request.getUserId(), MAX_TASKS_PER_USER);
        }

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .userId(request.getUserId())
                .priority(request.getPriority().name())
                .status("PENDING")
                .dueDate(request.getDueDate())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        task = taskDataStore.save(task);
        logger.info("Task created with ID: {}", task.getId());

        return mapToTaskResponse(task);
    }

    @Override
    public TaskResponse updateTask(Long taskId, TaskUpdateRequest request) {
        Task task = taskDataStore.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        if (request.getTitle() != null) {
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
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }

        task.setUpdatedAt(LocalDateTime.now());
        task = taskDataStore.save(task);

        logger.info("Task {} updated", taskId);
        return mapToTaskResponse(task);
    }

    @Override
    public void deleteTask(Long taskId) {
        if (!taskDataStore.findById(taskId).isPresent()) {
            throw new TaskNotFoundException(taskId);
        }
        taskDataStore.deleteById(taskId);
        logger.info("Task {} deleted", taskId);
    }

    @Override
    public PagedTaskResponse getUserTasks(Long userId, int page, int size) {
        List<Task> tasks = taskDataStore.findByUserId(userId, page, size);
        Long totalElements = taskDataStore.countByUserId(userId);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        List<TaskResponse> content = tasks.stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());

        return PagedTaskResponse.builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }

    @Override
    public TaskCountResponse getTaskCount(Long userId) {
        Long count = taskDataStore.countByUserId(userId);
        return TaskCountResponse.builder()
                .userId(userId)
                .taskCount(count)
                .build();
    }

    @Override
    public BulkTaskResponse bulkCreateTasks(List<TaskCreateRequest> requests) {
        if (requests.size() > 100) {
            throw new IllegalArgumentException("Cannot create more than 100 tasks at once");
        }

        List<TaskResponse> successfulTasks = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (TaskCreateRequest request : requests) {
            try {
                TaskResponse response = createTask(request);
                successfulTasks.add(response);
            } catch (Exception e) {
                errors.add(String.format("Failed to create task '%s': %s", request.getTitle(), e.getMessage()));
            }
        }

        return BulkTaskResponse.builder()
                .successCount(successfulTasks.size())
                .failureCount(errors.size())
                .tasks(successfulTasks)
                .errors(errors)
                .build();
    }

    private boolean isValidTransition(String fromStatus, String toStatus) {
        if (fromStatus == null || toStatus == null) {
            return false;
        }
        return (fromStatus.equals("IN_PROGRESS") && toStatus.equals("DONE"))
                || (fromStatus.equals("TODO") && toStatus.equals("IN_PROGRESS"))
                || (fromStatus.equals("DONE") && toStatus.equals("IN_PROGRESS"));
    }

    private TaskResponse mapToTaskResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .userId(task.getUserId())
                .status(task.getStatus())
                .priority(task.getPriority())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}