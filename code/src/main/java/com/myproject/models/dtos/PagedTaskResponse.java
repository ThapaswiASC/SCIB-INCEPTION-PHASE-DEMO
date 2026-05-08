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
public class PagedTaskResponse {

    @JsonProperty("content")
    private List<TaskResponse> content;

    @JsonProperty("page")
    private Integer page;

    @JsonProperty("size")
    private Integer size;

    @JsonProperty("totalElements")
    private Long totalElements;

    @JsonProperty("totalPages")
    private Integer totalPages;
}