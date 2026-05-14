package com.myproject.services.impl;

import com.myproject.exceptions.ColumnNotFoundException;
import com.myproject.models.datastores.ColumnDataStore;
import com.myproject.models.dtos.BulkUpdateColumnCountsRequest;
import com.myproject.models.dtos.BulkUpdateColumnCountsResponse;
import com.myproject.models.dtos.ColumnStatsResponse;
import com.myproject.models.entities.Column;
import com.myproject.services.interfaces.ColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ColumnServiceImpl implements ColumnService {

    @Autowired
    private ColumnDataStore columnDataStore;

    @Override
    public ColumnStatsResponse getColumnStats(String columnId) {
        Column column = columnDataStore.findById(columnId)
                .orElseThrow(() -> new ColumnNotFoundException(columnId));

        return new ColumnStatsResponse(
                column.getId(),
                column.getTaskCount(),
                column.getLastUpdated()
        );
    }

    @Override
    public BulkUpdateColumnCountsResponse bulkUpdateColumnCounts(BulkUpdateColumnCountsRequest request) {
        List<String> updatedColumns = new ArrayList<>();

        for (BulkUpdateColumnCountsRequest.ColumnUpdate update : request.getUpdates()) {
            try {
                columnDataStore.incrementTaskCount(update.getColumnId(), update.getIncrement());
                updatedColumns.add(update.getColumnId());
            } catch (Exception e) {
                // Log error but continue processing
            }
        }

        return new BulkUpdateColumnCountsResponse(true, updatedColumns);
    }

    @Override
    public void updateColumnCounts(String oldColumnId, String newColumnId) {
        if (oldColumnId != null) {
            columnDataStore.incrementTaskCount(oldColumnId, -1);
        }
        if (newColumnId != null) {
            columnDataStore.incrementTaskCount(newColumnId, 1);
        }
    }
}
