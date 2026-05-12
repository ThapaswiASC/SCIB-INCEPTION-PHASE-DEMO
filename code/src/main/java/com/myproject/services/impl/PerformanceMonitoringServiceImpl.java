package com.myproject.services.impl;

import com.myproject.services.interfaces.PerformanceMonitoringService;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class PerformanceMonitoringServiceImpl implements PerformanceMonitoringService {

    private final ConcurrentLinkedQueue<Long> executionTimes = new ConcurrentLinkedQueue<>();
    private static final int MAX_SAMPLES = 1000;

    @Override
    public void recordTaskCreationTime(long executionTimeMs) {
        executionTimes.offer(executionTimeMs);
        
        // Keep only the last MAX_SAMPLES
        while (executionTimes.size() > MAX_SAMPLES) {
            executionTimes.poll();
        }
    }

    @Override
    public double getAverageTaskCreationTime() {
        if (executionTimes.isEmpty()) {
            return 0.0;
        }
        
        return executionTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);
    }

    @Override
    public long getMaxTaskCreationTime() {
        return executionTimes.stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);
    }
}