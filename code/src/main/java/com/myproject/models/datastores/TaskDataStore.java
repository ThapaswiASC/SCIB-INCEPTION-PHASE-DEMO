package com.myproject.models.datastores;

import com.myproject.models.entities.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskDataStore {
    Task save(Task task);
    Optional<Task> findById(UUID id);
    List<Task> findByUserId(String userId, int page, int size, String sort);
    long countByUserId(String userId);
    void deleteById(UUID id);
    boolean existsByIdAndUserId(UUID id, String userId);
}