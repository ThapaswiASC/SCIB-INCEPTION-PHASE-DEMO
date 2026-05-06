package com.myproject.services.interfaces;

import com.myproject.models.dtos.TaskCreateRequestValidated;

import java.util.List;

public interface ValidationService {

    List<String> validateTaskInput(TaskCreateRequestValidated request);

    void validateTaskLimit(Long userId);

    void validateStatusTransition(String fromStatus, String toStatus);
}