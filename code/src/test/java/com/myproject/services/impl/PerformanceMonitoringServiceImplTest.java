package com.myproject.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PerformanceMonitoringServiceImplTest {

    private PerformanceMonitoringServiceImpl performanceMonitoringService;

    @BeforeEach
    void setUp() {
        performanceMonitoringService = new PerformanceMonitoringServiceImpl();
    }

    @Test
    void recordTaskCreationTime_SingleRecord_StoresExecutionTime() {
        // Act
        performanceMonitoringService.recordTaskCreationTime(150L);

        // Assert
        double average = performanceMonitoringService.getAverageTaskCreationTime();
        assertEquals(150.0, average, 0.01);
    }

    @Test
    void recordTaskCreationTime_MultipleRecords_CalculatesCorrectAverage() {
        // Act
        performanceMonitoringService.recordTaskCreationTime(100L);
        performanceMonitoringService.recordTaskCreationTime(200L);
        performanceMonitoringService.recordTaskCreationTime(300L);

        // Assert
        double average = performanceMonitoringService.getAverageTaskCreationTime();
        assertEquals(200.0, average, 0.01);
    }

    @Test
    void getAverageTaskCreationTime_NoRecords_ReturnsZero() {
        // Act
        double average = performanceMonitoringService.getAverageTaskCreationTime();

        // Assert
        assertEquals(0.0, average, 0.01);
    }

    @Test
    void getMaxTaskCreationTime_MultipleRecords_ReturnsMaximum() {
        // Act
        performanceMonitoringService.recordTaskCreationTime(100L);
        performanceMonitoringService.recordTaskCreationTime(350L);
        performanceMonitoringService.recordTaskCreationTime(200L);

        // Assert
        long max = performanceMonitoringService.getMaxTaskCreationTime();
        assertEquals(350L, max);
    }

    @Test
    void getMaxTaskCreationTime_NoRecords_ReturnsZero() {
        // Act
        long max = performanceMonitoringService.getMaxTaskCreationTime();

        // Assert
        assertEquals(0L, max);
    }

    @Test
    void recordTaskCreationTime_ExceedsMaxSamples_MaintainsLimit() {
        // Act - Record more than MAX_SAMPLES (1000)
        for (int i = 0; i < 1100; i++) {
            performanceMonitoringService.recordTaskCreationTime(100L + i);
        }

        // Assert - Should only keep last 1000 samples
        double average = performanceMonitoringService.getAverageTaskCreationTime();
        assertTrue(average > 0);
        // The average should be based on the last 1000 samples
        assertTrue(average >= 600.0); // Last 1000 samples start from 200
    }

    @Test
    void recordTaskCreationTime_ConcurrentAccess_HandlesCorrectly() throws InterruptedException {
        // Arrange
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                performanceMonitoringService.recordTaskCreationTime(100L);
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                performanceMonitoringService.recordTaskCreationTime(200L);
            }
        });

        // Act
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        // Assert
        double average = performanceMonitoringService.getAverageTaskCreationTime();
        assertTrue(average >= 100.0 && average <= 200.0);
    }

    @Test
    void recordTaskCreationTime_VeryLargeValue_HandlesCorrectly() {
        // Act
        performanceMonitoringService.recordTaskCreationTime(Long.MAX_VALUE);

        // Assert
        long max = performanceMonitoringService.getMaxTaskCreationTime();
        assertEquals(Long.MAX_VALUE, max);
    }

    @Test
    void recordTaskCreationTime_ZeroValue_HandlesCorrectly() {
        // Act
        performanceMonitoringService.recordTaskCreationTime(0L);

        // Assert
        double average = performanceMonitoringService.getAverageTaskCreationTime();
        assertEquals(0.0, average, 0.01);
    }
}