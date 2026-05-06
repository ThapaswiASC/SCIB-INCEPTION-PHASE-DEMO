package com.myproject.services.impl;

import com.myproject.exceptions.*;
import com.myproject.models.datastores.ColumnDataStore;
import com.myproject.models.datastores.TaskDataStore;
import com.myproject.models.datastores.UserDataStore;
import com.myproject.models.dtos.*;
import com.myproject.models.entities.Task;
import com.myproject.models.entities.User;
import com.myproject.services.interfaces.ColumnService;
import com.myproject.services.interfaces.TaskService;
import com.myproject.services.interfaces.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    
    private final TaskDataStore taskDataStore;
    private final UserDataStore userDataStore;
    private final ColumnDataStore columnDataStore;
    private final ColumnService columnService;
    private final ValidationService validationService;
    
    private static final long MAX_TASKS_PER_USER = 10000;
    
    @Override
    public TaskResponse createTask(TaskCreateRequest request) {
        // Validate user exists
        User user = userDataStore.findById(request.getUserId())
            .orElseThrow(() -> new UserNotFoundException(request.getUserId()));
        
        // Check task limit
        Long currentTaskCount = taskDataStore.countByUserId(request.getUserId());
        if (currentTaskCount >= MAX_TASKS_PER_USER) {
            throw new TaskLimitExceededException(request.getUserId(), currentTaskCount, MAX_TASKS_PER_USER);
        }
        
        // Sanitize inputs
        String sanitizedTitle = validationService.sanitizeInput(request.getTitle());
        String sanitizedDescription = validationService.sanitizeInput(request.getDescription());
        
        // Create task
        Task task = Task.builder()
            .title(sanitizedTitle)
            .description(sanitizedDescription)
            .userId(request.getUserId())
            .status("PENDING")
            .priority(request.getPriority().name())
            .dueDate(request.getDueDate())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .version(0L)
            .build();
        
        Task savedTask = taskDataStore.save(task);
        
        // Update user task count
        userDataStore.incrementTaskCount(request.getUserId(), 1);
        
        return mapToTaskResponse(savedTask);
    }
    
    @Override
    public TaskResponse createTaskValidated(TaskCreateRequestValidated request) {
        // Validate input
        ValidationResponse validation = validationService.validate(request);
        if (!validation.getValid()) {
            throw new InvalidInputException("Validation failed: " + String.join(", ", validation.getErrors()));
        }
        
        // Sanitize inputs
        String sanitizedTitle = validationService.sanitizeInput(request.getTitle());
        String sanitizedDescription = validationService.sanitizeInput(request.getDescription());
        
        // Create task
        Task task = Task.builder()
            .title(sanitizedTitle)
            .description(sanitizedDescription)
            .userId(1L) // Default user for validated tasks
            .status(request.getStatus().name())
            .priority(request.getPriority().name())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .version(0L)
            .build();
        
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
    public TaskDetailsResponse getTaskDetails(String taskId) {
        Long id = Long.parseLong(taskId);
        Task task = taskDataStore.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(taskId));
        
        return TaskDetailsResponse.builder()
            .taskId(task.getId().toString())
            .title(task.getTitle())
            .status(task.getStatus())
            .columnId(task.getColumnId())
            .build();
    }
    
    @Override
    public PagedTaskResponse getUserTasks(Long userId, int page, int size) {
        // Validate user exists
        userDataStore.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        List<Task> tasks = taskDataStore.findByUserIdPaginated(userId, page, size);
        Long totalElements = taskDataStore.countByUserId(userId);
        int totalPages = (int) Math.ceil((double) totalElements / size);
        
        List<TaskResponse> taskResponses = tasks.stream()
            .map(this::mapToTaskResponse)
            .collect(Collectors.toList());
        
        return PagedTaskResponse.builder()
            .content(taskResponses)
            .totalElements(totalElements)
            .totalPages(totalPages)
            .currentPage(page)
            .pageSize(size)
            .build();
    }
    
    @Override
    public TaskCountResponse getTaskCount(Long userId) {
        // Validate user exists
        userDataStore.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        Long count = taskDataStore.countByUserId(userId);
        
        return TaskCountResponse.builder()
            .userId(userId)
            .taskCount(count)
            .build();
    }
    
    @Override
    public TaskResponse updateTask(Long taskId, TaskUpdateRequest request) {
        Task task = taskDataStore.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException(taskId));
        
        if (request.getTitle() != null) {
            validationService.validateTitle(request.getTitle());
            task.setTitle(validationService.sanitizeInput(request.getTitle()));
        }
        
        if (request.getDescription() != null) {
            validationService.validateDescription(request.getDescription());
            task.setDescription(validationService.sanitizeInput(request.getDescription()));
        }
        
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        
        task.setUpdatedAt(LocalDateTime.now());
        Task updatedTask = taskDataStore.save(task);
        
        return mapToTaskResponse(updatedTask);
    }
    
    @Override
    public UpdateTaskStatusResponse updateTaskStatus(String taskId, UpdateTaskStatusRequest request) {
        Long id = Long.parseLong(taskId);
        Task task = taskDataStore.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(taskId));
        
        String oldStatus = task.getStatus();
        String oldColumnId = task.getColumnId();
        String newStatus = request.getStatus().name();
        String newColumnId = request.getColumnId();
        
        // Validate status transition
        validateStatusTransition(oldStatus, newStatus);
        
        // Update task
        task.setStatus(newStatus);
        task.setColumnId(newColumnId);
        task.setUpdatedAt(LocalDateTime.now());
        
        Task updatedTask = taskDataStore.save(task);
        
        // Update column counts
        columnService.updateColumnCounts(oldColumnId, newColumnId);
        
        return UpdateTaskStatusResponse.builder()
            .taskId(updatedTask.getId().toString())
            .status(updatedTask.getStatus())
            .updatedAt(updatedTask.getUpdatedAt())
            .build();
    }
    
    @Override
    public void deleteTask(Long taskId) {
        Task task = taskDataStore.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException(taskId));
        
        taskDataStore.deleteById(taskId);
        
        // Update user task count
        userDataStore.incrementTaskCount(task.getUserId(), -1);
        
        // Update column count if task was in a column
        if (task.getColumnId() != null) {
            columnDataStore.incrementTaskCount(task.getColumnId(), -1);
        }
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
        
        return BulkTaskResponse.builder()
            .successCount(successCount)
            .failureCount(failureCount)
            .createdTasks(createdTasks)
            .errors(errors)
            .build();
    }
    
    @Override
    public ValidationResponse validateTaskInput(TaskCreateRequestValidated request) {
        return validationService.validate(request);
    }
    
    private void validateStatusTransition(String fromStatus, String toStatus) {
        // Define allowed transitions
        if (fromStatus == null) {
            return; // New task, any status is allowed
        }
        
        boolean validTransition = false;
        
        switch (fromStatus) {
            case "TO_DO":
                validTransition = toStatus.equals("IN_PROGRESS");
                break;
            case "IN_PROGRESS":
                validTransition = toStatus.equals("DONE") || toStatus.equals("TO_DO");
                break;
            case "DONE":
                validTransition = toStatus.equals("IN_PROGRESS");
                break;
            default:
                validTransition = true; // Allow any transition for other statuses
        }
        
        if (!validTransition) {
            throw new InvalidStatusTransitionException(fromStatus, toStatus);
        }
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