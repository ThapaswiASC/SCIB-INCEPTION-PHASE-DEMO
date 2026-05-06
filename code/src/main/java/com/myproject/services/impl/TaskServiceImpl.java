package com.myproject.services.impl;

import com.myproject.exceptions.TaskNotFoundException;
import com.myproject.models.datastores.TaskRepository;
import com.myproject.models.dtos.*;
import com.myproject.models.entities.Task;
import com.myproject.services.interfaces.ColumnService;
import com.myproject.services.interfaces.TaskService;
import com.myproject.services.interfaces.ValidationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ValidationService validationService;
    private final ColumnService columnService;

    public TaskServiceImpl(TaskRepository taskRepository, 
                          ValidationService validationService,
                          ColumnService columnService) {
        this.taskRepository = taskRepository;
        this.validationService = validationService;
        this.columnService = columnService;
    }

    @Override
    public TaskResponse createTask(TaskCreateRequest request) {
        validationService.validateTaskLimit(request.getUserId());

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setUserId(request.getUserId());
        task.setPriority(request.getPriority());
        task.setStatus("PENDING");
        task.setDueDate(request.getDueDate());

        Task savedTask = taskRepository.save(task);
        return mapToTaskResponse(savedTask);
    }

    @Override
    public TaskResponse createTaskValidated(TaskCreateRequestValidated request) {
        List<String> errors = validationService.validateTaskInput(request);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setStatus("PENDING");

        Task savedTask = taskRepository.save(task);
        return mapToTaskResponse(savedTask);
    }

    @Override
    public PagedTaskResponse getUserTasks(Long userId, Pageable pageable) {
        Page<Task> taskPage = taskRepository.findByUserId(userId, pageable);

        PagedTaskResponse response = new PagedTaskResponse();
        response.setContent(taskPage.getContent().stream()
            .map(this::mapToTaskResponse)
            .collect(Collectors.toList()));
        response.setTotalElements(taskPage.getTotalElements());
        response.setTotalPages(taskPage.getTotalPages());
        response.setSize(taskPage.getSize());
        response.setNumber(taskPage.getNumber());

        return response;
    }

    @Override
    public TaskCountResponse getTaskCount(Long userId) {
        Long count = taskRepository.countTasksByUserId(userId);

        TaskCountResponse response = new TaskCountResponse();
        response.setUserId(userId);
        response.setTaskCount(count);

        return response;
    }

    @Override
    public TaskResponse getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException(taskId));

        return mapToTaskResponse(task);
    }

    @Override
    public TaskDetailsResponse getTaskDetails(String taskId) {
        Long id = Long.parseLong(taskId);
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(taskId));

        TaskDetailsResponse response = new TaskDetailsResponse();
        response.setTaskId(taskId);
        response.setTitle(task.getTitle());
        response.setStatus(task.getStatus());
        response.setColumnId(task.getColumnId());

        return response;
    }

    @Override
    public TaskResponse updateTask(Long taskId, TaskUpdateRequest request) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException(taskId));

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            if (task.getStatus() != null) {
                validationService.validateStatusTransition(task.getStatus(), request.getStatus());
            }
            task.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }

        Task updatedTask = taskRepository.save(task);
        return mapToTaskResponse(updatedTask);
    }

    @Override
    public UpdateTaskStatusResponse updateTaskStatus(String taskId, UpdateTaskStatusRequest request) {
        Long id = Long.parseLong(taskId);
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(taskId));

        String oldStatus = task.getStatus();
        String oldColumnId = task.getColumnId();

        if (oldStatus != null) {
            validationService.validateStatusTransition(oldStatus, request.getStatus());
        }

        task.setStatus(request.getStatus());
        task.setColumnId(request.getColumnId());

        Task updatedTask = taskRepository.save(task);

        // Update column counts
        columnService.updateColumnCounts(oldColumnId, request.getColumnId());

        UpdateTaskStatusResponse response = new UpdateTaskStatusResponse();
        response.setTaskId(taskId);
        response.setStatus(updatedTask.getStatus());
        response.setUpdatedAt(updatedTask.getUpdatedAt());

        return response;
    }

    @Override
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException(taskId));

        taskRepository.delete(task);
    }

    @Override
    public BulkTaskResponse bulkCreateTasks(List<TaskCreateRequest> requests) {
        List<TaskResponse> createdTasks = new ArrayList<>();
        List<BulkTaskResponse.FailureInfo> failures = new ArrayList<>();

        for (int i = 0; i < requests.size(); i++) {
            try {
                TaskCreateRequest request = requests.get(i);
                TaskResponse response = createTask(request);
                createdTasks.add(response);
            } catch (Exception e) {
                BulkTaskResponse.FailureInfo failure = new BulkTaskResponse.FailureInfo();
                failure.setIndex(i);
                failure.setError(e.getMessage());
                failures.add(failure);
            }
        }

        BulkTaskResponse response = new BulkTaskResponse();
        response.setCreatedTasks(createdTasks);
        response.setTotalCreated(createdTasks.size());
        response.setFailures(failures);

        return response;
    }

    @Override
    public ValidationResponse validateTaskInput(TaskCreateRequestValidated request) {
        List<String> errors = validationService.validateTaskInput(request);

        ValidationResponse response = new ValidationResponse();
        response.setValid(errors.isEmpty());
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
        return response;
    }
}