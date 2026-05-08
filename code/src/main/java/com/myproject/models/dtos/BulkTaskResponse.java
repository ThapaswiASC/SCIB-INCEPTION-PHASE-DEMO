package com.myproject.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("successCount")
    private Integer successCount;

    @JsonProperty("failureCount")
    private Integer failureCount;

    @JsonProperty("tasks")
    private List<TaskResponse> tasks;

    @JsonProperty("errors")
    private List<String> errors;
}