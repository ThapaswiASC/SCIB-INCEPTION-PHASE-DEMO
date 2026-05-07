package com.myproject.services.impl;

import com.myproject.exceptions.InvalidInputException;
import com.myproject.exceptions.InvalidStatusTransitionException;
import com.myproject.exceptions.TaskLimitExceededException;
import com.myproject.models.datastores.TaskRepository;
import com.myproject.models.dtos.TaskCreateRequest;
import com.myproject.models.dtos.TaskUpdateRequest;
import com.myproject.services.interfaces.ValidationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ValidationServiceImpl implements ValidationService {

    @Autowired
    private TaskRepository taskRepository;

    @Value("${app.task.max-per-user:10000}")
    private int maxTasksPerUser;

    private static final List<String> VALID_STATUSES = Arrays.asList(
        "PENDING", "IN_PROGRESS", "COMPLETED", "CANCELLED", "TO_DO", "DONE"
    );

    private static final List<String> VALID_PRIORITIES = Arrays.asList(
        "LOW", "MEDIUM", "HIGH", "URGENT"
    );

    @Override
    public void validateTaskInput(TaskCreateRequest request) {
        if (request.getTitle() == null || StringUtils.isBlank(request.getTitle())) {
            throw new InvalidInputException("Title cannot be empty or contain only whitespace");
        }

        if (request.getTitle().trim().isEmpty()) {
            throw new InvalidInputException("Title cannot be empty or contain only whitespace");
        }

        if (request.getDescription() != null && request.getDescription().length() > 10000) {
            throw new InvalidInputException("Description exceeds maximum length of 10000 characters");
        }

        if (request.getPriority() != null && !VALID_PRIORITIES.contains(request.getPriority())) {
            throw new InvalidInputException("Invalid priority. Must be one of: " + String.join(", ", VALID_PRIORITIES));
        }
    }

    @Override
    public void validateTaskUpdate(TaskUpdateRequest request) {
        if (request.getTitle() != null && StringUtils.isBlank(request.getTitle())) {
            throw new InvalidInputException("Title cannot be empty or contain only whitespace");
        }

        if (request.getDescription() != null && request.getDescription().length() > 10000) {
            throw new InvalidInputException("Description exceeds maximum length of 10000 characters");
        }

        if (request.getStatus() != null && !VALID_STATUSES.contains(request.getStatus())) {
            throw new InvalidInputException("Invalid status. Must be one of: " + String.join(", ", VALID_STATUSES));
        }

        if (request.getPriority() != null && !VALID_PRIORITIES.contains(request.getPriority())) {
            throw new InvalidInputException("Invalid priority. Must be one of: " + String.join(", ", VALID_PRIORITIES));
        }
    }

    @Override
    public void validateTaskLimit(Long userId) {
        Long taskCount = taskRepository.countTasksByUserId(userId);
        if (taskCount >= maxTasksPerUser) {
            throw new TaskLimitExceededException(
                String.format("User has reached maximum task limit of %d tasks", maxTasksPerUser)
            );
        }
    }

    @Override
    public void validateStatusTransition(String oldStatus, String newStatus) {
        if (oldStatus == null || newStatus == null) {
            return;
        }

        // Define valid transitions
        boolean isValidTransition = false;
        
        switch (oldStatus) {
            case "TO_DO":
            case "PENDING":
                isValidTransition = newStatus.equals("IN_PROGRESS");
                break;
            case "IN_PROGRESS":
                isValidTransition = newStatus.equals("DONE") || 
                                  newStatus.equals("COMPLETED") || 
                                  newStatus.equals("TO_DO") ||
                                  newStatus.equals("PENDING");
                break;
            case "DONE":
            case "COMPLETED":
                isValidTransition = newStatus.equals("IN_PROGRESS");
                break;
            case "CANCELLED":
                isValidTransition = newStatus.equals("PENDING") || newStatus.equals("TO_DO");
                break;
            default:
                isValidTransition = true;
        }

        if (!isValidTransition) {
            throw new InvalidStatusTransitionException(oldStatus, newStatus);
        }
    }
}
