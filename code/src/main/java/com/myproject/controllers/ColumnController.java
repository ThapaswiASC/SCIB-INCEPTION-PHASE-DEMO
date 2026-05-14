package com.myproject.controllers;

import com.myproject.models.dtos.*;
import com.myproject.services.interfaces.ColumnService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/columns")
public class ColumnController {

    private static final Logger logger = LoggerFactory.getLogger(ColumnController.class);

    @Autowired
    private ColumnService columnService;

    @GetMapping("/{columnId}/stats")
    public ResponseEntity<ColumnStatsResponse> getColumnStats(@PathVariable String columnId) {
        logger.info("GET /v1/columns/{}/stats - Fetching column statistics", columnId);
        ColumnStatsResponse response = columnService.getColumnStats(columnId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/bulk-update")
    public ResponseEntity<BulkUpdateColumnCountsResponse> bulkUpdateColumnCounts(
            @Valid @RequestBody BulkUpdateColumnCountsRequest request) {
        logger.info("PUT /v1/columns/bulk-update - Bulk updating column counts");
        BulkUpdateColumnCountsResponse response = columnService.bulkUpdateColumnCounts(request);
        return ResponseEntity.ok(response);
    }
}