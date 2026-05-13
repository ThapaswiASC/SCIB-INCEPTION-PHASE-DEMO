package com.myproject.services.impl;

import com.myproject.exceptions.BusinessException;
import com.myproject.exceptions.ResourceNotFoundException;
import com.myproject.models.datastores.TaskDataStore;
import com.myproject.models.datastores.UserDataStore;
import com.myproject.models.dtos.*;
import com.myproject.models.entities.Task;
import com.myproject.models.entities.User;
import com.myproject.services.interfaces.ColumnService;
import com.myproject.services.interfaces.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {
    private final TaskDataStore taskDataStore;
    private final UserDataStore userDataStore;
    private final ColumnService columnService;
    private static final int MAX_TASKS_PER_USER = 10000;

    @Override
    public TaskResponse createTask(TaskCreateRequest request) {
        log.debug("Creating task for user: {}", request.getUserId());
        
        // Validate user exists
        User user = userDataStore.findById(request.getUserId())
                .orElseGet(() -> createDefaultUser(request.getUserId()));
        
        // Check task limit
        Long currentTaskCount = taskDataStore.countByUserId(request.getUserId());
        if (currentTaskCount >= MAX_TASKS_PER_USER) {
            throw new BusinessException(
                    "User cannot have more than " + MAX_TASKS_PER_USER + " tasks",
                    "TASK_LIMIT_EXCEEDED",
                    HttpStatus.BAD_REQUEST
            );
        }
        
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .userId(request.getUserId())
                .priority(request.getPriority().name())
                .status("PENDING")
                .dueDate(request.getDueDate())
                .build();
        
        Task savedTask = taskDataStore.save(task);
        userDataStore.incrementTaskCount(request.getUserId());
        
        log.info("Task created successfully with ID: {}", savedTask.getId());
        return mapToResponse(savedTask);
    }

    @Override
    public TaskResponse getTaskById(Long taskId) {
        Task task = taskDataStore.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        return mapToResponse(task);
    }

    @Override
    public PagedTaskResponse getUserTasks(Long userId, Pageable pageable) {
        List<Task> tasks = taskDataStore.findByUserId(userId, pageable.getPageNumber(), pageable.getPageSize());
        Long totalElements = taskDataStore.countByUserId(userId);
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());
        
        return PagedTaskResponse.builder()
                .content(tasks.stream().map(this::mapToResponse).collect(Collectors.toList()))
                .totalElements(totalElements)
                .totalPages(totalPages)
                .currentPage(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
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
    public TaskResponse updateTask(Long taskId, TaskUpdateRequest request) {
        Task task = taskDataStore.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority().name());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus().name());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        
        Task updatedTask = taskDataStore.save(task);
        log.info("Task updated successfully: {}", taskId);
        return mapToResponse(updatedTask);
    }

    @Override
    public void deleteTask(Long taskId) {
        Task task = taskDataStore.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        
        taskDataStore.deleteById(taskId);
        userDataStore.decrementTaskCount(task.getUserId());
        log.info("Task deleted successfully: {}", taskId);
    }

    @Override
    public BulkTaskResponse bulkCreateTasks(List<TaskCreateRequest> requests) {
        List<TaskResponse> createdTasks = new ArrayList<>();
        List<BulkTaskResponse.BulkError> errors = new ArrayList<>();
        
        for (int i = 0; i < requests.size(); i++) {
            try {
                TaskResponse response = createTask(requests.get(i));
                createdTasks.add(response);
            } catch (Exception e) {
                errors.add(BulkTaskResponse.BulkError.builder()
                        .index(i)
                        .message(e.getMessage())
                        .build());
            }
        }
        
        return BulkTaskResponse.builder()
                .successCount(createdTasks.size())
                .failureCount(errors.size())
                .createdTasks(createdTasks)
                .errors(errors)
                .build();
    }

    @Override
    public TaskStatusUpdateResponse updateTaskStatus(String taskId, TaskStatusUpdateRequest request) {
        Long id = Long.parseLong(taskId);
        Task task = taskDataStore.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        
        String oldColumnId = task.getColumnId();
        task.setStatus(request.getStatus().name());
        task.setColumnId(request.getColumnId());
        
        Task updatedTask = taskDataStore.save(task);
        
        // Update column counts
        if (oldColumnId != null && !oldColumnId.equals(request.getColumnId())) {
            columnService.updateColumnCounts(oldColumnId, request.getColumnId());
        }
        
        return TaskStatusUpdateResponse.builder()
                .taskId(taskId)
                .status(updatedTask.getStatus())
                .updatedAt(updatedTask.getUpdatedAt())
                .build();
    }

    @Override
    public TaskDetailsResponse getTaskDetails(String taskId) {
        Long id = Long.parseLong(taskId);
        Task task = taskDataStore.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        
        return TaskDetailsResponse.builder()
                .taskId(taskId)
                .title(task.getTitle())
                .status(task.getStatus())
                .columnId(task.getColumnId())
                .build();
    }

    @Override
    public TaskResponse createTaskValidated(TaskCreateRequestValidated request) {
        log.debug("Creating validated task with title: {}", request.getTitle());
        
        // Convert validated request to standard create request
        TaskCreateRequest createRequest = TaskCreateRequest.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .userId(1L) // Default user ID for validated requests
                .priority(TaskCreateRequest.TaskPriority.valueOf(request.getPriority().name()))
                .build();
        
        return createTask(createRequest);
    }

    @Override
    public ValidationResponse validateTaskInput(TaskCreateRequestValidated request) {
        log.debug("Validating task input for title: {}", request.getTitle());
        
        List<String> errors = new ArrayList<>();
        
        // Validate title
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            errors.add("Title is required and cannot be empty");
        } else if (request.getTitle().length() > 255) {
            errors.add("Title cannot exceed 255 characters");
        }
        
        // Validate description
        if (request.getDescription() != null && request.getDescription().length() > 10000) {
            errors.add("Description cannot exceed 10000 characters");
        }
        
        // Validate priority
        if (request.getPriority() == null) {
            errors.add("Priority is required");
        }
        
        // Validate status
        if (request.getStatus() == null) {
            errors.add("Status is required");
        }
        
        if (errors.isEmpty()) {
            return ValidationResponse.builder()
                    .valid(true)
                    .message("Validation successful")
                    .build();
        } else {
            return ValidationResponse.builder()
                    .valid(false)
                    .message(String.join("; ", errors))
                    .build();
        }
    }

    private TaskResponse mapToResponse(Task task) {
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

    private User createDefaultUser(Long userId) {
        User user = User.builder()
                .id(userId)
                .username("user" + userId)
                .email("user" + userId + "@example.com")
                .taskCount(0L)
                .build();
        return userDataStore.save(user);
    }
}