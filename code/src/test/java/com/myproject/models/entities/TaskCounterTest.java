package com.myproject.models.entities;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskCounterTest {

    @Test
    void defaultConstructor_InitializesFieldsCorrectly() {
        // Act
        TaskCounter counter = new TaskCounter();

        // Assert
        assertNull(counter.getUserId());
        assertEquals(0L, counter.getTaskCount());
        assertNotNull(counter.getLastUpdated());
        assertEquals(0L, counter.getVersion());
    }

    @Test
    void parameterizedConstructor_SetsUserId() {
        // Act
        TaskCounter counter = new TaskCounter("user123");

        // Assert
        assertEquals("user123", counter.getUserId());
        assertEquals(0L, counter.getTaskCount());
        assertNotNull(counter.getLastUpdated());
        assertEquals(0L, counter.getVersion());
    }

    @Test
    void incrementCount_IncreasesTaskCountAndVersion() {
        // Arrange
        TaskCounter counter = new TaskCounter("user123");
        LocalDateTime beforeIncrement = counter.getLastUpdated();

        // Act
        counter.incrementCount();

        // Assert
        assertEquals(1L, counter.getTaskCount());
        assertEquals(1L, counter.getVersion());
        assertTrue(counter.getLastUpdated().isAfter(beforeIncrement) || 
                   counter.getLastUpdated().isEqual(beforeIncrement));
    }

    @Test
    void incrementCount_MultipleTimes_IncreasesCorrectly() {
        // Arrange
        TaskCounter counter = new TaskCounter("user123");

        // Act
        counter.incrementCount();
        counter.incrementCount();
        counter.incrementCount();

        // Assert
        assertEquals(3L, counter.getTaskCount());
        assertEquals(3L, counter.getVersion());
    }

    @Test
    void decrementCount_DecreasesTaskCountAndVersion() {
        // Arrange
        TaskCounter counter = new TaskCounter("user123");
        counter.incrementCount();
        counter.incrementCount();
        LocalDateTime beforeDecrement = counter.getLastUpdated();

        // Act
        counter.decrementCount();

        // Assert
        assertEquals(1L, counter.getTaskCount());
        assertEquals(3L, counter.getVersion()); // Version increments on both increment and decrement
        assertTrue(counter.getLastUpdated().isAfter(beforeDecrement) || 
                   counter.getLastUpdated().isEqual(beforeDecrement));
    }

    @Test
    void decrementCount_WhenCountIsZero_DoesNotDecrementBelowZero() {
        // Arrange
        TaskCounter counter = new TaskCounter("user123");

        // Act
        counter.decrementCount();

        // Assert
        assertEquals(0L, counter.getTaskCount());
    }

    @Test
    void decrementCount_WhenCountIsOne_DecreasesToZero() {
        // Arrange
        TaskCounter counter = new TaskCounter("user123");
        counter.incrementCount();

        // Act
        counter.decrementCount();

        // Assert
        assertEquals(0L, counter.getTaskCount());
    }

    @Test
    void settersAndGetters_WorkCorrectly() {
        // Arrange
        TaskCounter counter = new TaskCounter();
        LocalDateTime now = LocalDateTime.now();

        // Act
        counter.setUserId("user456");
        counter.setTaskCount(100L);
        counter.setLastUpdated(now);
        counter.setVersion(5L);

        // Assert
        assertEquals("user456", counter.getUserId());
        assertEquals(100L, counter.getTaskCount());
        assertEquals(now, counter.getLastUpdated());
        assertEquals(5L, counter.getVersion());
    }

    @Test
    void incrementAndDecrementCycle_MaintainsCorrectCount() {
        // Arrange
        TaskCounter counter = new TaskCounter("user123");

        // Act
        counter.incrementCount(); // 1
        counter.incrementCount(); // 2
        counter.incrementCount(); // 3
        counter.decrementCount(); // 2
        counter.incrementCount(); // 3
        counter.decrementCount(); // 2
        counter.decrementCount(); // 1

        // Assert
        assertEquals(1L, counter.getTaskCount());
    }
}