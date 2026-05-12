package com.myproject.services.impl;

import com.myproject.exceptions.ValidationException;
import com.myproject.models.dtos.TaskValidationRequest;
import com.myproject.models.dtos.ValidationResponse;
import com.myproject.services.interfaces.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ValidationServiceImpl implements ValidationService {

    @Override
    public ValidationResponse validateInput(TaskValidationRequest request) {
        List<String> errors = new ArrayList<>();
        
        // Validate title
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            errors.add("Title is required and cannot be empty or contain only whitespace");
        } else if (request.getTitle().length() > 255) {
            errors.add("Title cannot exceed 255 characters");
        }
        
        // Validate description
        if (request.getDescription() != null && request.getDescription().length() > 10000) {
            errors.add("Description cannot exceed 10000 characters");
        }
        
        // Validate priority
        if (request.getPriority() != null && !request.getPriority().matches("^(HIGH|MEDIUM|LOW)$")) {
            errors.add("Priority must be HIGH, MEDIUM, or LOW");
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

    @Override
    public void validateTaskInput(TaskValidationRequest request) {
        // Validate title
        if (request.getTitle() == null) {
            throw new ValidationException("Title is required");
        }
        if (request.getTitle().trim().isEmpty()) {
            throw new ValidationException("Title cannot be empty or contain only whitespace");
        }
        if (request.getTitle().length() > 255) {
            throw new ValidationException("Title cannot exceed 255 characters");
        }
        
        // Validate description
        if (request.getDescription() != null && request.getDescription().length() > 10000) {
            throw new ValidationException("Description cannot exceed 10000 characters");
        }
        
        // Validate priority
        if (request.getPriority() != null && !request.getPriority().matches("^(HIGH|MEDIUM|LOW)$")) {
            throw new ValidationException("Priority must be HIGH, MEDIUM, or LOW");
        }
        
        log.debug("Task input validation passed for title: {}", request.getTitle());
    }
}