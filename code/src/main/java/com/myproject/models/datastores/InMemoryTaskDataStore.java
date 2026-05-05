package com.myproject.models.datastores;

import com.myproject.models.entities.Task;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
public class InMemoryTaskDataStore implements TaskDataStore {
    private final Map<Long, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Task save(Task task) {
        if (task.getId() == null) {
            task.setId(idGenerator.getAndIncrement());
            task.setCreatedAt(LocalDateTime.now());
            task.setVersion(0L);
        }
        task.setUpdatedAt(LocalDateTime.now());
        task.setVersion(task.getVersion() + 1);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public Optional<Task> findById(String id) {
        try {
            Long taskId = Long.parseLong(id);
            return findById(taskId);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Task> findByUserId(Long userId, int page, int size) {
        return tasks.values().stream()
                .filter(task -> task.getUserId().equals(userId))
                .sorted(Comparator.comparing(Task::getCreatedAt).reversed())
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    @Override
    public Long countByUserId(Long userId) {
        return tasks.values().stream()
                .filter(task -> task.getUserId().equals(userId))
                .count();
    }

    @Override
    public void deleteById(Long id) {
        tasks.remove(id);
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Task> saveAll(List<Task> taskList) {
        List<Task> savedTasks = new ArrayList<>();
        for (Task task : taskList) {
            savedTasks.add(save(task));
        }
        return savedTasks;
    }
}