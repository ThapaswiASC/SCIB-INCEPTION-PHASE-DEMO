package com.myproject.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDetailsResponse {
    private String taskId;
    private String title;
    private String status;
    private String columnId;
}