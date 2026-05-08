package com.myproject.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateTaskMoveRequest {

    @NotNull(message = "From status is required")
    @JsonProperty("fromStatus")
    private TaskStatus fromStatus;

    @NotNull(message = "To status is required")
    @JsonProperty("toStatus")
    private TaskStatus toStatus;

    public enum TaskStatus {
        TODO,
        IN_PROGRESS,
        DONE
    }
}