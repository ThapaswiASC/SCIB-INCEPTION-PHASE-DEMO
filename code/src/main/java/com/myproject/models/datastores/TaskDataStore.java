package com.myproject.models.datastores;

import com.myproject.models.entities.Task;
import java.util.List;
import java.util.Optional;

public interface TaskDataStore {
    Task save(Task task);
    Optional<Task> findById(Long id);
    List<Task> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
}