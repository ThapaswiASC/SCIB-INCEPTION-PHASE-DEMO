package com.myproject.services.impl;

import com.myproject.exceptions.ColumnNotFoundException;
import com.myproject.models.datastores.ColumnRepository;
import com.myproject.models.dtos.BulkUpdateColumnCountsRequest;
import com.myproject.models.dtos.BulkUpdateColumnCountsResponse;
import com.myproject.models.dtos.ColumnStatsResponse;
import com.myproject.models.entities.Column;
import com.myproject.services.interfaces.ColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ColumnServiceImpl implements ColumnService {

    @Autowired
    private ColumnRepository columnRepository;

    @Override
    public ColumnStatsResponse getColumnStats(String columnId) {
        Column column = columnRepository.findById(columnId)
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
            try {
                columnRepository.incrementTaskCount(update.getColumnId(), update.getIncrement());
                updatedColumns.add(update.getColumnId());
            } catch (Exception e) {
                // Log error but continue processing other updates
            }
        }

        BulkUpdateColumnCountsResponse response = new BulkUpdateColumnCountsResponse();
        response.setSuccess(!updatedColumns.isEmpty());
        response.setUpdatedColumns(updatedColumns);

        return response;
    }

    @Override
    public void updateColumnCounts(String oldColumnId, String newColumnId) {
        if (oldColumnId != null && !oldColumnId.isEmpty()) {
            columnRepository.incrementTaskCount(oldColumnId, -1);
        }
        if (newColumnId != null && !newColumnId.isEmpty()) {
            columnRepository.incrementTaskCount(newColumnId, 1);
        }
    }
}
