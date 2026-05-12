package com.myproject.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class PerformanceMonitor {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitor.class);
    private final ConcurrentHashMap<String, AtomicLong> metrics = new ConcurrentHashMap<>();

    public void recordTaskCreation(long executionTimeMs) {
        metrics.computeIfAbsent("task.creation.count", k -> new AtomicLong(0)).incrementAndGet();
        metrics.computeIfAbsent("task.creation.total.time", k -> new AtomicLong(0)).addAndGet(executionTimeMs);
        
        if (executionTimeMs > 200) {
            logger.warn("Task creation exceeded performance threshold: {}ms", executionTimeMs);
        } else {
            logger.debug("Task creation completed in {}ms", executionTimeMs);
        }
    }

    public long getAverageCreationTime() {
        long count = metrics.getOrDefault("task.creation.count", new AtomicLong(0)).get();
        long totalTime = metrics.getOrDefault("task.creation.total.time", new AtomicLong(0)).get();
        return count > 0 ? totalTime / count : 0;
    }

    public long getTotalTasksCreated() {
        return metrics.getOrDefault("task.creation.count", new AtomicLong(0)).get();
    }
}
