package com.myproject.services.impl;

import com.myproject.exceptions.ResourceNotFoundException;
import com.myproject.models.datastores.ColumnDataStore;
import com.myproject.models.dtos.BulkColumnUpdateRequest;
import com.myproject.models.dtos.BulkColumnUpdateResponse;
import com.myproject.models.dtos.ColumnStatsResponse;
import com.myproject.models.entities.Column;
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
        Column column = columnDataStore.findById(columnId)
                .orElseGet(() -> createDefaultColumn(columnId));
        
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
                Column column = columnDataStore.findById(update.getColumnId())
                        .orElseGet(() -> createDefaultColumn(update.getColumnId()));
                
                columnDataStore.incrementTaskCount(update.getColumnId(), update.getIncrement());
                updatedColumns.add(update.getColumnId());
                log.debug("Updated column {} with increment {}", update.getColumnId(), update.getIncrement());
            } catch (Exception e) {
                log.error("Failed to update column {}: {}", update.getColumnId(), e.getMessage());
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

    private Column createDefaultColumn(String columnId) {
        Column column = Column.builder()
                .id(columnId)
                .name(columnId)
                .taskCount(0)
                .position(0)
                .lastUpdated(LocalDateTime.now())
                .build();
        return columnDataStore.save(column);
    }
}