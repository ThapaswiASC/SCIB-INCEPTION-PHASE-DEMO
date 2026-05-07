package com.myproject.models.datastores;

import com.myproject.models.entities.Task;
import com.myproject.models.dtos.TaskStatus;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
public class InMemoryTaskDataStore implements TaskDataStore {

    private final Map<Long, Task> taskStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Task save(Task task) {
        if (task.getId() == null) {
            task.setId(idGenerator.getAndIncrement());
        }
        taskStore.put(task.getId(), task);
        return task;
    }

    @Override
    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(taskStore.get(id));
    }

    @Override
    public List<Task> findByStatus(TaskStatus status) {
        return taskStore.values().stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteById(Long id) {
        return taskStore.remove(id) != null;
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(taskStore.values());
    }
}
