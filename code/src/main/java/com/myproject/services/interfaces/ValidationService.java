package com.myproject.services.interfaces;

import com.myproject.models.dtos.TaskCreateRequestValidated;
import com.myproject.models.dtos.ValidationResponse;

public interface ValidationService {
    ValidationResponse validate(TaskCreateRequestValidated request);
    void validateTitle(String title);
    void validateDescription(String description);
    String sanitizeInput(String input);
}