package com.myproject.models.datastores;

import com.myproject.models.entities.Task;

import java.util.List;
import java.util.Optional;

public interface TaskDataStore {
    Task save(Task task);
    Optional<Task> findById(Long id);
    List<Task> findByUserId(Long userId);
    List<Task> findByUserIdPaginated(Long userId, int page, int size);
    Long countByUserId(Long userId);
    List<Task> findByColumnId(String columnId);
    void deleteById(Long id);
    List<Task> saveAll(List<Task> tasks);
    List<Task> findAll();
}