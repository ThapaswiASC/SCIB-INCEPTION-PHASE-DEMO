package com.myproject.services.impl;

import com.myproject.exceptions.InvalidInputException;
import com.myproject.exceptions.InvalidStatusTransitionException;
import com.myproject.exceptions.TaskLimitExceededException;
import com.myproject.models.datastores.TaskDataStore;
import com.myproject.models.dtos.TaskCreateRequest;
import com.myproject.services.interfaces.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ValidationServiceImpl implements ValidationService {

    private static final int MAX_TASKS_PER_USER = 10000;
    private static final List<String> VALID_STATUS_TRANSITIONS = Arrays.asList(
            "TO_DO->IN_PROGRESS",
            "IN_PROGRESS->DONE",
            "IN_PROGRESS->TO_DO",
            "PENDING->IN_PROGRESS",
            "IN_PROGRESS->COMPLETED"
    );

    @Autowired
    private TaskDataStore taskDataStore;

    @Override
    public void validateTaskInput(TaskCreateRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new InvalidInputException("Title is required and cannot be empty");
        }

        validateTitle(request.getTitle());

        if (request.getDescription() != null && request.getDescription().length() > 10000) {
            throw new InvalidInputException("Description exceeds maximum length of 10000 characters");
        }
    }

    @Override
    public void validateStatusTransition(String fromStatus, String toStatus) {
        if (fromStatus == null || toStatus == null) {
            return;
        }

        String transition = fromStatus + "->" + toStatus;
        if (!VALID_STATUS_TRANSITIONS.contains(transition)) {
            throw new InvalidStatusTransitionException(fromStatus, toStatus);
        }
    }

    @Override
    public void validateTaskLimit(Long userId) {
        Long currentCount = taskDataStore.countByUserId(userId);
        if (currentCount >= MAX_TASKS_PER_USER) {
            throw new TaskLimitExceededException(
                    "User has reached the maximum task limit of " + MAX_TASKS_PER_USER
            );
        }
    }

    @Override
    public void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidInputException("Title cannot be empty or contain only whitespace");
        }

        if (title.length() > 255) {
            throw new InvalidInputException("Title exceeds maximum length of 255 characters");
        }

        // Check for whitespace-only content
        if (title.trim().length() == 0) {
            throw new InvalidInputException("Title cannot contain only whitespace");
        }
    }
}
