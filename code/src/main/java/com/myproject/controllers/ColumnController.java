package com.myproject.controllers;

import com.myproject.models.dtos.BulkColumnUpdateRequest;
import com.myproject.models.dtos.BulkColumnUpdateResponse;
import com.myproject.models.dtos.ColumnStatsResponse;
import com.myproject.services.interfaces.ColumnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/columns")
@RequiredArgsConstructor
@Slf4j
public class ColumnController {
    private final ColumnService columnService;

    @GetMapping("/{columnId}/stats")
    public ResponseEntity<ColumnStatsResponse> getColumnStats(@PathVariable String columnId) {
        log.info("Getting stats for column: {}", columnId);
        ColumnStatsResponse response = columnService.getColumnStats(columnId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/bulk-update")
    public ResponseEntity<BulkColumnUpdateResponse> bulkUpdateColumnCounts(
            @Valid @RequestBody BulkColumnUpdateRequest request) {
        log.info("Bulk updating column counts for {} columns", request.getUpdates().size());
        BulkColumnUpdateResponse response = columnService.bulkUpdateColumnCounts(request);
        return ResponseEntity.ok(response);
    }
}