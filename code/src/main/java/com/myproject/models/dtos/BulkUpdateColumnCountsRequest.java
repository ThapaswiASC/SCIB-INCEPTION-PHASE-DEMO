package com.myproject.models.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkUpdateColumnCountsRequest {
    
    @NotNull(message = "Updates list is required")
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