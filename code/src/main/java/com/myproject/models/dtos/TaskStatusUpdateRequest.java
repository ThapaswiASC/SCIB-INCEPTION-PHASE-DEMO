package com.myproject.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatusUpdateRequest {

    @NotNull(message = "Status is required")
    @JsonProperty("status")
    private TaskStatus status;

    @NotBlank(message = "Column ID cannot be blank")
    @Size(max = 50, message = "Column ID must be less than 50 characters")
    @JsonProperty("columnId")
    private String columnId;

    public enum TaskStatus {
        TODO,
        IN_PROGRESS,
        DONE
    }
}