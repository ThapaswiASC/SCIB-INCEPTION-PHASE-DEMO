package com.myproject.services.impl;

import com.myproject.exceptions.ColumnNotFoundException;
import com.myproject.models.datastores.ColumnDataStore;
import com.myproject.models.dtos.BulkUpdateColumnCountsRequest;
import com.myproject.models.dtos.BulkUpdateColumnCountsResponse;
import com.myproject.models.dtos.ColumnStatsResponse;
import com.myproject.models.dtos.ColumnTaskCountResponse;
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
    public ColumnTaskCountResponse getColumnTaskCount(String columnId) {
        Long count = columnDataStore.countByColumnId(columnId);
        
        if (count == null) {
            throw new ColumnNotFoundException(columnId);
        }

        return ColumnTaskCountResponse.builder()
                .columnId(columnId)
                .taskCount(count.intValue())
                .build();
    }

    @Override
    public ColumnStatsResponse getColumnStats(String columnId) {
        Column column = columnDataStore.findById(columnId)
                .orElseThrow(() -> new ColumnNotFoundException(columnId));

        ColumnStatsResponse response = new ColumnStatsResponse();
        response.setColumnId(column.getId());
        response.setTaskCount(column.getTaskCount());
        response.setLastUpdated(column.getLastUpdated());
        return response;
    }

    @Override
    public BulkUpdateColumnCountsResponse bulkUpdateColumnCounts(BulkUpdateColumnCountsRequest request) {
        List<String> updatedColumns = new ArrayList<>();

        for (BulkUpdateColumnCountsRequest.ColumnUpdate update : request.getUpdates()) {
            Column column = columnDataStore.findById(update.getColumnId())
                    .orElseThrow(() -> new ColumnNotFoundException(update.getColumnId()));

            columnDataStore.incrementTaskCount(update.getColumnId(), update.getIncrement());
            updatedColumns.add(update.getColumnId());
        }

        BulkUpdateColumnCountsResponse response = new BulkUpdateColumnCountsResponse();
        response.setSuccess(true);
        response.setUpdatedColumns(updatedColumns);
        return response;
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