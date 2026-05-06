package com.myproject.controllers;

import com.myproject.models.dtos.BulkUpdateColumnCountsRequest;
import com.myproject.models.dtos.BulkUpdateColumnCountsResponse;
import com.myproject.models.dtos.ColumnStatsResponse;
import com.myproject.services.interfaces.ColumnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/columns")
@RequiredArgsConstructor
public class ColumnController {
    
    private final ColumnService columnService;
    
    @GetMapping("/{columnId}/stats")
    public ResponseEntity<ColumnStatsResponse> getColumnStats(@PathVariable String columnId) {
        ColumnStatsResponse response = columnService.getColumnStats(columnId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/bulk-update")
    public ResponseEntity<BulkUpdateColumnCountsResponse> bulkUpdateColumnCounts(
            @Valid @RequestBody BulkUpdateColumnCountsRequest request) {
        BulkUpdateColumnCountsResponse response = columnService.bulkUpdateColumnCounts(request);
        return ResponseEntity.ok(response);
    }
}