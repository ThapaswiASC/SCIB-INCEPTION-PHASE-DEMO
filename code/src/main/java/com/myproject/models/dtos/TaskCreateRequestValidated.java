package com.myproject.models.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateRequestValidated {
    
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    @Pattern(regexp = "^(?!\\s*$).+", message = "Title cannot be empty or whitespace only")
    private String title;
    
    @Size(max = 10000, message = "Description cannot exceed 10000 characters")
    private String description;
    
    @NotNull(message = "Priority is required")
    private TaskPriorityValidated priority;
    
    @NotNull(message = "Status is required")
    private TaskStatusValidated status;
    
    public enum TaskPriorityValidated {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }
    
    public enum TaskStatusValidated {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
}