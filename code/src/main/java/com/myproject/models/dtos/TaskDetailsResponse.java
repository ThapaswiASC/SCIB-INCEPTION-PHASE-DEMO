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
public class TaskDetailsResponse {

    @JsonProperty("taskId")
    private Long taskId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("status")
    private String status;

    @JsonProperty("assignee")
    private String assignee;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("columnId")
    private String columnId;
}