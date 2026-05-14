package com.myproject.models.datastores;

import com.myproject.models.entities.Column;
import java.util.List;
import java.util.Optional;

public interface ColumnDataStore {

    Column save(Column column);

    Optional<Column> findById(String id);

    List<Column> findAll();

    void deleteById(String id);

    boolean existsById(String id);

    void incrementTaskCount(String columnId);

    void decrementTaskCount(String columnId);
}