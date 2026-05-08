package com.myproject.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateRequest {

    @NotBlank(message = "Task title is required")
    @Size(min = 1, max = 255, message = "Task title must be between 1-255 characters")
    @JsonProperty("title")
    private String title;

    @Size(max = 2000, message = "Task description cannot exceed 2000 characters")
    @JsonProperty("description")
    private String description;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    @Min(value = 1, message = "User ID must be at least 1")
    @JsonProperty("userId")
    private Long userId;

    @NotNull(message = "Priority is required")
    @JsonProperty("priority")
    private TaskPriority priority;

    @JsonProperty("dueDate")
    private LocalDateTime dueDate;

    public enum TaskPriority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }
}