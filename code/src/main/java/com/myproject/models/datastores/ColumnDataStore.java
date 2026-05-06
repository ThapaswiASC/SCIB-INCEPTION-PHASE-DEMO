package com.myproject.models.datastores;

import com.myproject.models.entities.BoardColumn;
import java.util.Optional;

public interface ColumnDataStore {
    BoardColumn save(BoardColumn column);
    Optional<BoardColumn> findById(String id);
    void incrementTaskCount(String columnId, int increment);
}