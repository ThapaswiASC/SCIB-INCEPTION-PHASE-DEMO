package com.myproject.models.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkColumnUpdateRequest {
    @NotEmpty(message = "Updates list cannot be empty")
    @Valid
    private List<ColumnUpdate> updates;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColumnUpdate {
        private String columnId;
        private Integer increment;
    }
}