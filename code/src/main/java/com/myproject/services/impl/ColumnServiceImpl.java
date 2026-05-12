package com.myproject.services.impl;

import com.myproject.exceptions.ColumnNotFoundException;
import com.myproject.models.datastores.ColumnDataStore;
import com.myproject.models.dtos.BulkColumnUpdateRequest;
import com.myproject.models.dtos.BulkColumnUpdateResponse;
import com.myproject.models.dtos.ColumnStatsResponse;
import com.myproject.models.entities.BoardColumn;
import com.myproject.services.interfaces.ColumnService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ColumnServiceImpl implements ColumnService {
    private final ColumnDataStore columnDataStore;

    @Override
    public ColumnStatsResponse getColumnStats(String columnId) {
        BoardColumn column = columnDataStore.findById(columnId)
                .orElseThrow(() -> new ColumnNotFoundException(columnId));
        
        return ColumnStatsResponse.builder()
                .columnId(column.getId())
                .taskCount(column.getTaskCount())
                .lastUpdated(column.getLastUpdated())
                .build();
    }

    @Override
    public BulkColumnUpdateResponse bulkUpdateColumnCounts(BulkColumnUpdateRequest request) {
        List<String> updatedColumns = new ArrayList<>();
        
        for (BulkColumnUpdateRequest.ColumnUpdate update : request.getUpdates()) {
            try {
                BoardColumn column = columnDataStore.findById(update.getColumnId())
                        .orElseThrow(() -> new ColumnNotFoundException(update.getColumnId()));
                
                columnDataStore.incrementTaskCount(update.getColumnId(), update.getIncrement());
                updatedColumns.add(update.getColumnId());
                log.debug("Updated column {} with increment {}", update.getColumnId(), update.getIncrement());
            } catch (Exception e) {
                log.error("Failed to update column {}: {}", update.getColumnId(), e.getMessage());
                throw e;
            }
        }
        
        return BulkColumnUpdateResponse.builder()
                .success(true)
                .updatedColumns(updatedColumns)
                .build();
    }

    @Override
    public void updateColumnCounts(String oldColumnId, String newColumnId) {
        if (oldColumnId != null) {
            columnDataStore.incrementTaskCount(oldColumnId, -1);
        }
        if (newColumnId != null) {
            columnDataStore.incrementTaskCount(newColumnId, 1);
        }
        log.debug("Updated column counts: {} -> {}", oldColumnId, newColumnId);
    }
}
