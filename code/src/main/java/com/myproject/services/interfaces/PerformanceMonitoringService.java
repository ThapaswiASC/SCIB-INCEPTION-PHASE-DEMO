package com.myproject.services.interfaces;

public interface PerformanceMonitoringService {
    void recordTaskCreationTime(long executionTimeMs);
    double getAverageTaskCreationTime();
    long getMaxTaskCreationTime();
}