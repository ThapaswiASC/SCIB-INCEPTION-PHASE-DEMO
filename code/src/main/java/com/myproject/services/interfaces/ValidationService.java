package com.myproject.services.interfaces;

import com.myproject.models.dtos.TaskCreateRequest;
import com.myproject.models.dtos.TaskUpdateRequest;

public interface ValidationService {
    void validateTaskInput(TaskCreateRequest request);
    void validateTaskUpdate(TaskUpdateRequest request);
    void validateTaskLimit(Long userId);
    void validateStatusTransition(String oldStatus, String newStatus);
}
