package com.myproject.services.interfaces;

import com.myproject.models.dtos.TaskValidationRequest;
import com.myproject.models.dtos.ValidationResponse;

public interface ValidationService {
    ValidationResponse validateInput(TaskValidationRequest request);
    void validateTaskInput(TaskValidationRequest request);
}