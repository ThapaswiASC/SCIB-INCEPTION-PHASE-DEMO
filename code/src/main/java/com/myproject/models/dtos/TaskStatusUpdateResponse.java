package com.myproject.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatusUpdateResponse {

    @JsonProperty("taskId")
    private Long taskId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    @JsonProperty("message")
    private String message;
}