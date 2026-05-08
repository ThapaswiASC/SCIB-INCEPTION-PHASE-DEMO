package com.myproject.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskSummary {

    @JsonProperty("taskId")
    private Long taskId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("status")
    private String status;
}