package com.myproject.services.impl;

import com.myproject.exceptions.InvalidInputException;
import com.myproject.models.dtos.TaskCreateRequestValidated;
import com.myproject.models.dtos.ValidationResponse;
import com.myproject.services.interfaces.ValidationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ValidationServiceImpl implements ValidationService {
    
    private static final int MAX_TITLE_LENGTH = 255;
    private static final int MAX_DESCRIPTION_LENGTH = 10000;
    
    @Override
    public ValidationResponse validate(TaskCreateRequestValidated request) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        // Validate title
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            errors.add("Title is required and cannot be empty or whitespace only");
        } else if (request.getTitle().length() > MAX_TITLE_LENGTH) {
            errors.add("Title cannot exceed " + MAX_TITLE_LENGTH + " characters");
        }
        
        // Validate description
        if (request.getDescription() != null && request.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            errors.add("Description cannot exceed " + MAX_DESCRIPTION_LENGTH + " characters");
        }
        
        // Validate priority
        if (request.getPriority() == null) {
            errors.add("Priority is required");
        }
        
        // Validate status
        if (request.getStatus() == null) {
            errors.add("Status is required");
        }
        
        return ValidationResponse.builder()
            .valid(errors.isEmpty())
            .errors(errors)
            .warnings(warnings)
            .build();
    }
    
    @Override
    public void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidInputException("Title is required and cannot be empty or whitespace only");
        }
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new InvalidInputException("Title cannot exceed " + MAX_TITLE_LENGTH + " characters");
        }
    }
    
    @Override
    public void validateDescription(String description) {
        if (description != null && description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new InvalidInputException("Description cannot exceed " + MAX_DESCRIPTION_LENGTH + " characters");
        }
    }
    
    @Override
    public String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        // Remove potential XSS threats while preserving special characters
        return input.replaceAll("<script>", "")
                   .replaceAll("</script>", "")
                   .replaceAll("<iframe>", "")
                   .replaceAll("</iframe>", "")
                   .trim();
    }
}