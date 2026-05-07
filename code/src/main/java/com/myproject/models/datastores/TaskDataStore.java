package com.myproject.models.datastores;

import com.myproject.models.entities.Task;
import com.myproject.models.dtos.TaskStatus;

import java.util.List;
import java.util.Optional;

public interface TaskDataStore {

    Task save(Task task);

    Optional<Task> findById(Long id);

    List<Task> findByStatus(TaskStatus status);

    boolean deleteById(Long id);

    List<Task> findAll();
}
