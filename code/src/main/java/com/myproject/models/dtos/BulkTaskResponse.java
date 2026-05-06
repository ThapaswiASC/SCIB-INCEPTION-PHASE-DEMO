package com.myproject.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkTaskResponse {
    private Integer successCount;
    private Integer failureCount;
    private List<TaskResponse> createdTasks;
    private List<String> errors;
}