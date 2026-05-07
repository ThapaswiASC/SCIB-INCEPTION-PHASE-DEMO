package com.myproject.controllers;

import com.myproject.models.dtos.BulkUpdateColumnCountsRequest;
import com.myproject.models.dtos.BulkUpdateColumnCountsResponse;
import com.myproject.models.dtos.ColumnStatsResponse;
import com.myproject.services.interfaces.ColumnService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
public class ColumnController {

    @Autowired
    private ColumnService columnService;

    @GetMapping("/columns/{columnId}/stats")
    public ResponseEntity<ColumnStatsResponse> getColumnStatistics(@PathVariable String columnId) {
        ColumnStatsResponse response = columnService.getColumnStats(columnId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/columns/bulk-update")
    public ResponseEntity<BulkUpdateColumnCountsResponse> bulkUpdateColumnCounts(
            @Valid @RequestBody BulkUpdateColumnCountsRequest request) {
        BulkUpdateColumnCountsResponse response = columnService.bulkUpdateColumnCounts(request);
        return ResponseEntity.ok(response);
    }
}