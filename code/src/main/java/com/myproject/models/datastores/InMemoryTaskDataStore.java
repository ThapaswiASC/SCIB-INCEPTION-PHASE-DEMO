package com.myproject.models.datastores;

import com.myproject.models.entities.Task;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryTaskDataStore implements TaskDataStore {

    private final Map<Long, Task> taskStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Task save(Task task) {
        if (task.getId() == null) {
            task.setId(idGenerator.getAndIncrement());
            task.setCreatedAt(LocalDateTime.now());
        }
        task.setUpdatedAt(LocalDateTime.now());
        taskStore.put(task.getId(), task);
        return task;
    }

    @Override
    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(taskStore.get(id));
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(taskStore.values());
    }

    @Override
    public void deleteById(Long id) {
        taskStore.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return taskStore.containsKey(id);
    }
}