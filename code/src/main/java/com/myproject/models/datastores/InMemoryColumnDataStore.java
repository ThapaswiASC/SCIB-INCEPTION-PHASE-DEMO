package com.myproject.models.datastores;

import com.myproject.models.entities.Column;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryColumnDataStore implements ColumnDataStore {
    private final Map<String, Column> columns = new ConcurrentHashMap<>();

    @Override
    public Column save(Column column) {
        column.setLastUpdated(LocalDateTime.now());
        columns.put(column.getId(), column);
        return column;
    }

    @Override
    public Optional<Column> findById(String id) {
        return Optional.ofNullable(columns.get(id));
    }

    @Override
    public void incrementTaskCount(String columnId, int increment) {
        Column column = columns.get(columnId);
        if (column != null) {
            column.setTaskCount(column.getTaskCount() + increment);
            column.setLastUpdated(LocalDateTime.now());
        }
    }
}