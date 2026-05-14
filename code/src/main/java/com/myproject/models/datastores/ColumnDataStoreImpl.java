package com.myproject.models.datastores;

import com.myproject.models.entities.Column;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ColumnDataStoreImpl implements ColumnDataStore {

    private final Map<String, Column> columnStore = new ConcurrentHashMap<>();

    public ColumnDataStoreImpl() {
        // Initialize default columns
        Column todoColumn = new Column("to-do", "To Do", "default-board", 0);
        Column inProgressColumn = new Column("in-progress", "In Progress", "default-board", 1);
        Column doneColumn = new Column("done", "Done", "default-board", 2);
        
        columnStore.put(todoColumn.getId(), todoColumn);
        columnStore.put(inProgressColumn.getId(), inProgressColumn);
        columnStore.put(doneColumn.getId(), doneColumn);
    }

    @Override
    public Column save(Column column) {
        columnStore.put(column.getId(), column);
        return column;
    }

    @Override
    public Optional<Column> findById(String id) {
        return Optional.ofNullable(columnStore.get(id));
    }

    @Override
    public List<Column> findAll() {
        return new ArrayList<>(columnStore.values());
    }

    @Override
    public void deleteById(String id) {
        columnStore.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return columnStore.containsKey(id);
    }

    @Override
    public void incrementTaskCount(String columnId) {
        Column column = columnStore.get(columnId);
        if (column != null) {
            column.incrementTaskCount();
        }
    }

    @Override
    public void decrementTaskCount(String columnId) {
        Column column = columnStore.get(columnId);
        if (column != null) {
            column.decrementTaskCount();
        }
    }
}