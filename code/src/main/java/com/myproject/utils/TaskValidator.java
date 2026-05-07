package com.myproject.utils;

import com.myproject.exceptions.ValidationException;
import com.myproject.models.dtos.TaskCreateRequest;
import com.myproject.models.dtos.TaskUpdateRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TaskValidator {

    public void validateCreateRequest(TaskCreateRequest request) {
        List<String> errors = new ArrayList<>();

        if (request == null) {
            throw new ValidationException("Request cannot be null");
        }

        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            errors.add("Title is required");
        } else if (request.getTitle().length() > 200) {
            errors.add("Title must not exceed 200 characters");
        }

        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            errors.add("Description is required");
        } else if (request.getDescription().length() > 1000) {
            errors.add("Description must not exceed 1000 characters");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed", errors);
        }
    }

    public void validateUpdateRequest(TaskUpdateRequest request) {
        List<String> errors = new ArrayList<>();

        if (request == null) {
            throw new ValidationException("Request cannot be null");
        }

        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            errors.add("Title is required");
        } else if (request.getTitle().length() > 200) {
            errors.add("Title must not exceed 200 characters");
        }

        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            errors.add("Description is required");
        } else if (request.getDescription().length() > 1000) {
            errors.add("Description must not exceed 1000 characters");
        }

        if (request.getStatus() == null) {
            errors.add("Status is required");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed", errors);
        }
    }
}
