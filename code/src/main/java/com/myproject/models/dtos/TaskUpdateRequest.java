package com.myproject.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateRequest {

    @Size(min = 1, max = 255, message = "Task title must be between 1-255 characters")
    @JsonProperty("title")
    private String title;

    @Size(max = 2000, message = "Task description cannot exceed 2000 characters")
    @JsonProperty("description")
    private String description;

    @JsonProperty("status")
    private TaskStatus status;

    @JsonProperty("priority")
    private TaskPriority priority;

    @JsonProperty("dueDate")
    private LocalDateTime dueDate;

    public enum TaskStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }

    public enum TaskPriority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }
}