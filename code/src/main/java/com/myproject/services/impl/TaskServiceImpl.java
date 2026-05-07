package com.myproject.services.impl;

import com.myproject.exceptions.TaskNotFoundException;
import com.myproject.models.datastores.TaskRepository;
import com.myproject.models.dtos.*;
import com.myproject.models.entities.Task;
import com.myproject.services.interfaces.TaskService;
import com.myproject.services.interfaces.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ValidationService validationService;

    @Override
    public TaskResponse createTask(TaskCreateRequest request) {
        validationService.validateTaskInput(request);
        validationService.validateTaskLimit(request.getUserId());

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setUserId(request.getUserId());
        task.setPriority(request.getPriority());
        task.setStatus("PENDING");

        Task savedTask = taskRepository.save(task);
        return mapToResponse(savedTask);
    }

    @Override
    public TaskResponse updateTask(Long id, TaskUpdateRequest request) {
        validationService.validateTaskUpdate(request);

        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            validationService.validateStatusTransition(task.getStatus(), request.getStatus());
            task.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }

        Task updatedTask = taskRepository.save(task);
        return mapToResponse(updatedTask);
    }

    @Override
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
        return mapToResponse(task);
    }

    @Override
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
    }

    @Override
    public PagedTaskResponse getUserTasks(Long userId, Pageable pageable) {
        Page<Task> taskPage = taskRepository.findByUserId(userId, pageable);

        PagedTaskResponse response = new PagedTaskResponse();
        response.setContent(taskPage.getContent().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList()));
        response.setTotalElements(taskPage.getTotalElements());
        response.setTotalPages(taskPage.getTotalPages());
        response.setCurrentPage(taskPage.getNumber());
        response.setPageSize(taskPage.getSize());

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
    public BulkTaskResponse bulkCreateTasks(List<TaskCreateRequest> requests) {
        BulkTaskResponse response = new BulkTaskResponse();
        List<TaskResponse> createdTasks = new ArrayList<>();
        List<BulkTaskResponse.BulkError> errors = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        for (int i = 0; i < requests.size(); i++) {
            try {
                TaskCreateRequest request = requests.get(i);
                TaskResponse taskResponse = createTask(request);
                createdTasks.add(taskResponse);
                successCount++;
            } catch (Exception e) {
                BulkTaskResponse.BulkError error = new BulkTaskResponse.BulkError();
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

    @Override
    public UpdateTaskStatusResponse updateTaskStatus(String taskId, UpdateTaskStatusRequest request) {
        Long id = Long.parseLong(taskId);
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(taskId));

        validationService.validateStatusTransition(task.getStatus(), request.getStatus());

        task.setStatus(request.getStatus());
        task.setColumnId(request.getColumnId());
        Task updatedTask = taskRepository.save(task);

        UpdateTaskStatusResponse response = new UpdateTaskStatusResponse();
        response.setTaskId(String.valueOf(updatedTask.getId()));
        response.setStatus(updatedTask.getStatus());
        response.setUpdatedAt(updatedTask.getUpdatedAt());

        return response;
    }

    @Override
    public TaskDetailsResponse getTaskDetails(String taskId) {
        Long id = Long.parseLong(taskId);
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(taskId));

        TaskDetailsResponse response = new TaskDetailsResponse();
        response.setTaskId(String.valueOf(task.getId()));
        response.setTitle(task.getTitle());
        response.setStatus(task.getStatus());
        response.setColumnId(task.getColumnId());

        return response;
    }

    private TaskResponse mapToResponse(Task task) {
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
