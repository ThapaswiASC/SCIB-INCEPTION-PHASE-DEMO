package com.myproject.services.impl;

import com.myproject.exceptions.InvalidStatusTransitionException;
import com.myproject.exceptions.TaskLimitExceededException;
import com.myproject.models.datastores.TaskRepository;
import com.myproject.models.dtos.TaskCreateRequestValidated;
import com.myproject.services.interfaces.ValidationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ValidationServiceImpl implements ValidationService {

    private final TaskRepository taskRepository;

    @Value("${app.task.max-per-user:10000}")
    private int maxTasksPerUser;

    @Value("${app.validation.title.max-length:255}")
    private int maxTitleLength;

    @Value("${app.validation.description.max-length:10000}")
    private int maxDescriptionLength;

    private static final List<String> VALID_PRIORITIES = Arrays.asList("LOW", "MEDIUM", "HIGH", "CRITICAL", "URGENT");
    private static final List<String> VALID_STATUSES = Arrays.asList("PENDING", "TO_DO", "IN_PROGRESS", "DONE", "COMPLETED", "CANCELLED");

    public ValidationServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<String> validateTaskInput(TaskCreateRequestValidated request) {
        List<String> errors = new ArrayList<>();

        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            errors.add("Title cannot be empty or contain only whitespace");
        } else if (request.getTitle().length() > maxTitleLength) {
            errors.add("Title cannot exceed " + maxTitleLength + " characters");
        }

        if (request.getDescription() != null && request.getDescription().length() > maxDescriptionLength) {
            errors.add("Description cannot exceed " + maxDescriptionLength + " characters");
        }

        if (request.getPriority() == null) {
            errors.add("Priority is required");
        } else if (!VALID_PRIORITIES.contains(request.getPriority().toUpperCase())) {
            errors.add("Priority must be one of: " + String.join(", ", VALID_PRIORITIES));
        }

        return errors;
    }

    @Override
    public void validateTaskLimit(Long userId) {
        Long currentCount = taskRepository.countTasksByUserId(userId);
        if (currentCount >= maxTasksPerUser) {
            throw new TaskLimitExceededException(userId, currentCount.intValue(), maxTasksPerUser);
        }
    }

    @Override
    public void validateStatusTransition(String fromStatus, String toStatus) {
        if (fromStatus == null || toStatus == null) {
            throw new InvalidStatusTransitionException(fromStatus, toStatus);
        }

        // Define valid transitions
        boolean isValid = false;
        switch (fromStatus.toUpperCase()) {
            case "TO_DO":
            case "PENDING":
                isValid = toStatus.equalsIgnoreCase("IN_PROGRESS");
                break;
            case "IN_PROGRESS":
                isValid = toStatus.equalsIgnoreCase("DONE") || 
                         toStatus.equalsIgnoreCase("COMPLETED") || 
                         toStatus.equalsIgnoreCase("TO_DO") ||
                         toStatus.equalsIgnoreCase("PENDING");
                break;
            case "DONE":
            case "COMPLETED":
                isValid = toStatus.equalsIgnoreCase("IN_PROGRESS");
                break;
            case "CANCELLED":
                isValid = false;
                break;
            default:
                isValid = VALID_STATUSES.contains(toStatus.toUpperCase());
        }

        if (!isValid) {
            throw new InvalidStatusTransitionException(fromStatus, toStatus);
        }
    }
}