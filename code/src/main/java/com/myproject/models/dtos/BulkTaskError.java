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
public class BulkTaskError {

    @JsonProperty("index")
    private Integer index;

    @JsonProperty("message")
    private String message;

    @JsonProperty("errorCode")
    private String errorCode;
}