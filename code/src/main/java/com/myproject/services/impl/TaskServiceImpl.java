package com.myproject.services.impl;

import com.myproject.exceptions.*;
import com.myproject.models.datastores.TaskDataStore;
import com.myproject.models.dtos.*;
import com.myproject.models.entities.Task;
import com.myproject.services.interfaces.ColumnService;
import com.myproject.services.interfaces.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private static final long MAX_TASKS_PER_USER = 10000L;
    private static final List<String> VALID_TRANSITIONS = Arrays.asList(
        "TO_DO->IN_PROGRESS",
        "IN_PROGRESS->DONE",
        "IN_PROGRESS->TO_DO",
        "PENDING->IN_PROGRESS",
        "IN_PROGRESS->COMPLETED",
        "IN_PROGRESS->CANCELLED"
    );

    @Autowired
    private TaskDataStore taskDataStore;

    @Autowired
    private ColumnService columnService;

    @Override
    public UpdateTaskStatusResponse updateTaskStatus(String taskId, UpdateTaskStatusRequest request) {
        Task task = taskDataStore.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        String oldStatus = task.getStatus() != null ? task.getStatus().name() : "TO_DO";
        String newStatus = request.getStatus().name();
        String transition = oldStatus + "->" + newStatus;

        if (!VALID_TRANSITIONS.contains(transition)) {
            throw new InvalidStatusTransitionException(oldStatus, newStatus);
        }

        String oldColumnId = task.getColumnId();
        task.setStatus(request.getStatus());
        task.setColumnId(request.getColumnId());
        task.setUpdatedAt(LocalDateTime.now());

        Task updatedTask = taskDataStore.save(task);

        if (oldColumnId != null && !oldColumnId.equals(request.getColumnId())) {
            columnService.updateColumnCounts(oldColumnId, request.getColumnId());
        }

        UpdateTaskStatusResponse response = new UpdateTaskStatusResponse();
        response.setTaskId(updatedTask.getId().toString());
        response.setStatus(updatedTask.getStatus());
        response.setUpdatedAt(updatedTask.getUpdatedAt());
        return response;
    }

    @Override
    public TaskDetailsResponse getTaskDetails(String taskId) {
        Task task = taskDataStore.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        TaskDetailsResponse response = new TaskDetailsResponse();
        response.setTaskId(task.getId().toString());
        response.setTitle(task.getTitle());
        response.setStatus(task.getStatus());
        response.setColumnId(task.getColumnId());
        return response;
    }

    @Override
    public TaskResponse createTask(TaskCreateRequest request) {
        Long currentCount = taskDataStore.countByUserId(request.getUserId());
        if (currentCount >= MAX_TASKS_PER_USER) {
            throw new TaskLimitExceededException(
                String.format("User %d has reached the maximum task limit of %d", 
                    request.getUserId(), MAX_TASKS_PER_USER)
            );
        }

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setUserId(request.getUserId());
        task.setPriority(request.getPriority());
        task.setStatus(TaskStatus.PENDING);
        task.setDueDate(request.getDueDate());

        Task savedTask = taskDataStore.save(task);
        return mapToTaskResponse(savedTask);
    }

    @Override
    public PagedTaskResponse getUserTasks(Long userId, int page, int size, String sort) {
        if (size > 100) {
            size = 100;
        }

        List<Task> tasks = taskDataStore.findByUserId(userId, page, size);
        Long totalElements = taskDataStore.countByUserId(userId);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        PagedTaskResponse response = new PagedTaskResponse();
        response.setContent(tasks.stream().map(this::mapToTaskResponse).toList());
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements(totalElements);
        response.setTotalPages(totalPages);
        response.setLast(page >= totalPages - 1);
        return response;
    }

    @Override
    public TaskCountResponse getTaskCount(Long userId) {
        Long count = taskDataStore.countByUserId(userId);
        if (count > MAX_TASKS_PER_USER) {
            count = MAX_TASKS_PER_USER;
        }

        TaskCountResponse response = new TaskCountResponse();
        response.setUserId(userId);
        response.setTaskCount(count);
        return response;
    }

    @Override
    public TaskResponse getTaskById(Long taskId) {
        Task task = taskDataStore.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
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
            task.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
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
        if (requests.size() > 100) {
            throw new TaskLimitExceededException("Bulk creation limited to 100 tasks at a time");
        }

        BulkTaskResponse response = new BulkTaskResponse();
        List<TaskResponse> createdTasks = new ArrayList<>();
        List<BulkTaskResponse.BulkTaskError> errors = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        for (int i = 0; i < requests.size(); i++) {
            try {
                TaskCreateRequest request = requests.get(i);
                TaskResponse taskResponse = createTask(request);
                createdTasks.add(taskResponse);
                successCount++;
            } catch (Exception e) {
                BulkTaskResponse.BulkTaskError error = new BulkTaskResponse.BulkTaskError();
                error.setIndex(i);
                error.setMessage(e.getMessage());
                errors.add(error);
                failureCount++;
            }
        }

        response.setSuccessCount(successCount);
        response.setFailureCount(failureCount);
        response.setCreatedTasks(createdTasks);
        response.setErrors(errors);
        return response;
    }

    private TaskResponse mapToTaskResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setUserId(task.getUserId());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        response.setVersion(task.getVersion());
        return response;
    }
}