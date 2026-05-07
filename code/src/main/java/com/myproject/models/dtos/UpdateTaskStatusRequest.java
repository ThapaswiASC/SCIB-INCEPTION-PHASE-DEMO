package com.myproject.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskStatusRequest {
    
    @NotNull(message = "Status is required")
    private TaskStatus status;
    
    @NotBlank(message = "Column ID is required")
    private String columnId;
}