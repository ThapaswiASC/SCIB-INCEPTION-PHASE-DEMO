package com.myproject.services.interfaces;

import com.myproject.models.dtos.TaskCreateRequest;

public interface ValidationService {
    void validateTaskInput(TaskCreateRequest request);
    void validateStatusTransition(String fromStatus, String toStatus);
    void validateTaskLimit(Long userId);
    void validateTitle(String title);
}
