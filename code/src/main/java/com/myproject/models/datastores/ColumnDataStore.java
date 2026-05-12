package com.myproject.models.datastores;

import com.myproject.models.entities.Column;
import java.util.Optional;

public interface ColumnDataStore {
    Column save(Column column);
    Optional<Column> findById(String id);
    void incrementTaskCount(String columnId, int increment);
    Long countByColumnId(String columnId);
}