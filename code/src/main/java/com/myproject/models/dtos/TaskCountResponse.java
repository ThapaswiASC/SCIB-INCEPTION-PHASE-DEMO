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
public class TaskCountResponse {

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("taskCount")
    private Long taskCount;
}