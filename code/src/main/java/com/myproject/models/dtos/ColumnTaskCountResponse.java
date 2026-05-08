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
public class ColumnTaskCountResponse {

    @JsonProperty("columnId")
    private String columnId;

    @JsonProperty("taskCount")
    private Integer taskCount;
}