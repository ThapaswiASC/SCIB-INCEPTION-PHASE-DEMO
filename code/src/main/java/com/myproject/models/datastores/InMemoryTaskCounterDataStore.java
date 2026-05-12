package com.myproject.models.datastores;

import com.myproject.models.entities.TaskCounter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryTaskCounterDataStore implements TaskCounterDataStore {

    private final Map<String, TaskCounter> counterStore = new ConcurrentHashMap<>();

    @Override
    public TaskCounter save(TaskCounter counter) {
        counter.setLastUpdated(LocalDateTime.now());
        counterStore.put(counter.getUserId(), counter);
        return counter;
    }

    @Override
    public Optional<TaskCounter> findByUserId(String userId) {
        return Optional.ofNullable(counterStore.get(userId));
    }

    @Override
    public synchronized void incrementTaskCount(String userId) {
        TaskCounter counter = counterStore.computeIfAbsent(userId, TaskCounter::new);
        counter.incrementCount();
        counterStore.put(userId, counter);
    }

    @Override
    public synchronized void decrementTaskCount(String userId) {
        TaskCounter counter = counterStore.get(userId);
        if (counter != null) {
            counter.decrementCount();
            counterStore.put(userId, counter);
        }
    }
}