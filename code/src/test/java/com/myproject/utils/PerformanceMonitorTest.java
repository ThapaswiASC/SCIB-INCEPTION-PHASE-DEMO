package com.myproject.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PerformanceMonitorTest {

    private PerformanceMonitor performanceMonitor;

    @BeforeEach
    void setUp() {
        performanceMonitor = new PerformanceMonitor();
    }

    @Test
    void recordTaskCreation_SingleRecord_UpdatesMetrics() {
        // Act
        performanceMonitor.recordTaskCreation(100L);

        // Assert
        assertEquals(1L, performanceMonitor.getTotalTasksCreated());
        assertEquals(100L, performanceMonitor.getAverageCreationTime());
    }

    @Test
    void recordTaskCreation_MultipleRecords_CalculatesCorrectAverage() {
        // Act
        performanceMonitor.recordTaskCreation(100L);
        performanceMonitor.recordTaskCreation(200L);
        performanceMonitor.recordTaskCreation(300L);

        // Assert
        assertEquals(3L, performanceMonitor.getTotalTasksCreated());
        assertEquals(200L, performanceMonitor.getAverageCreationTime());
    }

    @Test
    void recordTaskCreation_ZeroExecutionTime_HandlesCorrectly() {
        // Act
        performanceMonitor.recordTaskCreation(0L);

        // Assert
        assertEquals(1L, performanceMonitor.getTotalTasksCreated());
        assertEquals(0L, performanceMonitor.getAverageCreationTime());
    }

    @Test
    void recordTaskCreation_VeryLargeExecutionTime_HandlesCorrectly() {
        // Act
        performanceMonitor.recordTaskCreation(10000L);

        // Assert
        assertEquals(1L, performanceMonitor.getTotalTasksCreated());
        assertEquals(10000L, performanceMonitor.getAverageCreationTime());
    }

    @Test
    void getTotalTasksCreated_NoRecords_ReturnsZero() {
        // Act
        long total = performanceMonitor.getTotalTasksCreated();

        // Assert
        assertEquals(0L, total);
    }

    @Test
    void getTotalTasksCreated_MultipleRecords_ReturnsCorrectCount() {
        // Act
        for (int i = 0; i < 10; i++) {
            performanceMonitor.recordTaskCreation(100L);
        }

        // Assert
        assertEquals(10L, performanceMonitor.getTotalTasksCreated());
    }

    @Test
    void getAverageCreationTime_NoRecords_ReturnsZero() {
        // Act
        long average = performanceMonitor.getAverageCreationTime();

        // Assert
        assertEquals(0L, average);
    }

    @Test
    void getAverageCreationTime_MixedExecutionTimes_CalculatesCorrectly() {
        // Act
        performanceMonitor.recordTaskCreation(50L);
        performanceMonitor.recordTaskCreation(100L);
        performanceMonitor.recordTaskCreation(150L);
        performanceMonitor.recordTaskCreation(200L);

        // Assert
        assertEquals(4L, performanceMonitor.getTotalTasksCreated());
        assertEquals(125L, performanceMonitor.getAverageCreationTime());
    }

    @Test
    void recordTaskCreation_FastExecution_DoesNotLogWarning() {
        // Act - execution time below 200ms threshold
        performanceMonitor.recordTaskCreation(150L);

        // Assert
        assertEquals(1L, performanceMonitor.getTotalTasksCreated());
        assertEquals(150L, performanceMonitor.getAverageCreationTime());
    }

    @Test
    void recordTaskCreation_SlowExecution_LogsWarning() {
        // Act - execution time above 200ms threshold
        performanceMonitor.recordTaskCreation(250L);

        // Assert
        assertEquals(1L, performanceMonitor.getTotalTasksCreated());
        assertEquals(250L, performanceMonitor.getAverageCreationTime());
    }

    @Test
    void recordTaskCreation_ExactlyAtThreshold_DoesNotLogWarning() {
        // Act - execution time exactly at 200ms threshold
        performanceMonitor.recordTaskCreation(200L);

        // Assert
        assertEquals(1L, performanceMonitor.getTotalTasksCreated());
        assertEquals(200L, performanceMonitor.getAverageCreationTime());
    }

    @Test
    void recordTaskCreation_HighVolume_MaintainsAccuracy() {
        // Act - record 1000 tasks
        for (int i = 1; i <= 1000; i++) {
            performanceMonitor.recordTaskCreation(100L);
        }

        // Assert
        assertEquals(1000L, performanceMonitor.getTotalTasksCreated());
        assertEquals(100L, performanceMonitor.getAverageCreationTime());
    }

    @Test
    void recordTaskCreation_VaryingExecutionTimes_CalculatesCorrectAverage() {
        // Act
        performanceMonitor.recordTaskCreation(10L);
        performanceMonitor.recordTaskCreation(20L);
        performanceMonitor.recordTaskCreation(30L);
        performanceMonitor.recordTaskCreation(40L);
        performanceMonitor.recordTaskCreation(50L);

        // Assert
        assertEquals(5L, performanceMonitor.getTotalTasksCreated());
        assertEquals(30L, performanceMonitor.getAverageCreationTime()); // (10+20+30+40+50)/5 = 30
    }

    @Test
    void concurrentRecording_HandlesCorrectly() throws InterruptedException {
        // Arrange
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                performanceMonitor.recordTaskCreation(100L);
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                performanceMonitor.recordTaskCreation(200L);
            }
        });

        // Act
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        // Assert
        assertEquals(200L, performanceMonitor.getTotalTasksCreated());
        long average = performanceMonitor.getAverageCreationTime();
        assertTrue(average >= 100L && average <= 200L);
    }

    @Test
    void recordTaskCreation_SequentialCalls_UpdatesMetricsCorrectly() {
        // First record
        performanceMonitor.recordTaskCreation(100L);
        assertEquals(1L, performanceMonitor.getTotalTasksCreated());
        assertEquals(100L, performanceMonitor.getAverageCreationTime());

        // Second record
        performanceMonitor.recordTaskCreation(200L);
        assertEquals(2L, performanceMonitor.getTotalTasksCreated());
        assertEquals(150L, performanceMonitor.getAverageCreationTime());

        // Third record
        performanceMonitor.recordTaskCreation(300L);
        assertEquals(3L, performanceMonitor.getTotalTasksCreated());
        assertEquals(200L, performanceMonitor.getAverageCreationTime());
    }
}