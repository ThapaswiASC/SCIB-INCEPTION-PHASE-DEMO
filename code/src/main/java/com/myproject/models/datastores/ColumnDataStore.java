package com.myproject.models.datastores;

import com.myproject.models.entities.Column;

import java.util.List;
import java.util.Optional;

public interface ColumnDataStore {
    Column save(Column column);
    Optional<Column> findById(String id);
    List<Column> findByBoardId(String boardId);
    void incrementTaskCount(String columnId, int increment);
    List<Column> findAll();
}