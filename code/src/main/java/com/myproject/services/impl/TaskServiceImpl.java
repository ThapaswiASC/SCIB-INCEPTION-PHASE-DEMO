package com.myproject.services.impl;

import com.myproject.exceptions.InvalidInputException;
import com.myproject.exceptions.InvalidStatusTransitionException;
import com.myproject.exceptions.TaskLimitExceededException;
import com.myproject.exceptions.TaskNotFoundException;
import com.myproject.models.datastores.TaskRepository;
import com.myproject.models.dtos.*;
import com.myproject.models.entities.Task;
import com.myproject.services.interfaces.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private static final Long MAX_TASKS_PER_USER = 10000L;

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public TaskResponse createTask(TaskCreateRequest request) {
        // Validate input
        validateTaskInput(request);

        // Check task limit
        Long currentTaskCount = taskRepository.countByUserId(request.getUserId());
        if (currentTaskCount >= MAX_TASKS_PER_USER) {
            throw new TaskLimitExceededException(request.getUserId(), currentTaskCount, MAX_TASKS_PER_USER);
        }

        // Create task entity
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setUserId(request.getUserId());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setStatus("PENDING");

        // Save task
        Task savedTask = taskRepository.save(task);

        return mapToResponse(savedTask);
    }

    @Override
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
        return mapToResponse(task);
    }

    @Override
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public TaskResponse updateTask(Long id, TaskUpdateRequest request) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));

        // Update fields if provided
        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            validateTitle(request.getTitle());
            task.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            validateDescription(request.getDescription());
            task.setDescription(request.getDescription());
        }

        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }

        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }

        Task updatedTask = taskRepository.save(task);
        return mapToResponse(updatedTask);
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

        List<TaskResponse> content = taskPage.getContent()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());

        return new PagedTaskResponse(
            content,
            taskPage.getTotalElements(),
            taskPage.getTotalPages(),
            taskPage.getNumber(),
            taskPage.getSize()
        );
    }

    @Override
    public TaskCountResponse getTaskCount(Long userId) {
        Long count = taskRepository.countByUserId(userId);
        return new TaskCountResponse(userId, count);
    }

    @Override
    public UpdateTaskStatusResponse updateTaskStatus(String taskId, UpdateTaskStatusRequest request) {
        Long id = Long.parseLong(taskId);
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));

        String oldStatus = task.getStatus();
        String newStatus = request.getStatus();

        // Validate status transition
        validateStatusTransition(oldStatus, newStatus);

        task.setStatus(newStatus);
        task.setColumnId(request.getColumnId());
        task.setUpdatedAt(LocalDateTime.now());

        taskRepository.save(task);

        return new UpdateTaskStatusResponse(taskId, newStatus, task.getUpdatedAt());
    }

    @Override
    public TaskDetailsResponse getTaskDetails(String taskId) {
        Long id = Long.parseLong(taskId);
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));

        return new TaskDetailsResponse(
            taskId,
            task.getTitle(),
            task.getStatus(),
            task.getColumnId()
        );
    }

    // Helper methods
    private void validateTaskInput(TaskCreateRequest request) {
        validateTitle(request.getTitle());
        if (request.getDescription() != null) {
            validateDescription(request.getDescription());
        }
    }

    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidInputException("Title cannot be empty or contain only whitespace");
        }
        if (title.length() > 255) {
            throw new InvalidInputException("Title cannot exceed 255 characters");
        }
    }

    private void validateDescription(String description) {
        if (description != null && description.length() > 10000) {
            throw new InvalidInputException("Description cannot exceed 10000 characters");
        }
    }

    private void validateStatusTransition(String fromStatus, String toStatus) {
        // Define valid transitions
        if (fromStatus == null) {
            return; // Allow any status for new tasks
        }

        boolean isValidTransition = false;

        switch (fromStatus) {
            case "TO_DO":
            case "PENDING":
                isValidTransition = toStatus.equals("IN_PROGRESS");
                break;
            case "IN_PROGRESS":
                isValidTransition = toStatus.equals("DONE") || toStatus.equals("COMPLETED") || toStatus.equals("TO_DO");
                break;
            case "DONE":
            case "COMPLETED":
                isValidTransition = toStatus.equals("IN_PROGRESS");
                break;
        }

        if (!isValidTransition) {
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