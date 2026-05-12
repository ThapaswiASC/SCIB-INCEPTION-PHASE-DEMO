package com.myproject.models.datastores;

import com.myproject.models.entities.Task;

import java.util.List;
import java.util.Optional;

public interface TaskDataStore {
    Task save(Task task);
    Optional<Task> findById(Long id);
    List<Task> findByUserId(Long userId, int page, int size);
    Long countByUserId(Long userId);
    void deleteById(Long id);
    List<Task> findByColumnId(String columnId);
    List<Task> saveAll(List<Task> tasks);
}