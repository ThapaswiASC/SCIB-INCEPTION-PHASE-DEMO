package com.myproject.services.impl;

import com.myproject.exceptions.ColumnNotFoundException;
import com.myproject.models.datastores.ColumnDataStore;
import com.myproject.models.dtos.BulkUpdateColumnCountsRequest;
import com.myproject.models.dtos.BulkUpdateColumnCountsResponse;
import com.myproject.models.dtos.ColumnStatsResponse;
import com.myproject.models.entities.BoardColumn;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ColumnServiceImplTest {

    @Mock
    private ColumnDataStore columnDataStore;

    @InjectMocks
    private ColumnServiceImpl columnService;

    // ========== getColumnStats Tests ==========

    @Test
    void getColumnStats_ValidColumnId_ReturnsStats() {
        // Arrange
        String columnId = "to-do";
        BoardColumn column = new BoardColumn();
        column.setId(columnId);
        column.setName("To Do");
        column.setTaskCount(15);
        column.setLastUpdated(LocalDateTime.now());

        when(columnDataStore.findById(columnId)).thenReturn(Optional.of(column));

        // Act
        ColumnStatsResponse response = columnService.getColumnStats(columnId);

        // Assert
        assertNotNull(response);
        assertEquals(columnId, response.getColumnId());
        assertEquals(15, response.getTaskCount());
        assertNotNull(response.getLastUpdated());
        verify(columnDataStore).findById(columnId);
    }

    @Test
    void getColumnStats_ColumnNotFound_ThrowsException() {
        // Arrange
        String columnId = "invalid-column";
        when(columnDataStore.findById(columnId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ColumnNotFoundException.class, () -> {
            columnService.getColumnStats(columnId);
        });
        verify(columnDataStore).findById(columnId);
    }

    @Test
    void getColumnStats_InProgressColumn_ReturnsStats() {
        // Arrange
        String columnId = "in-progress";
        BoardColumn column = new BoardColumn();
        column.setId(columnId);
        column.setName("In Progress");
        column.setTaskCount(8);
        column.setLastUpdated(LocalDateTime.now());

        when(columnDataStore.findById(columnId)).thenReturn(Optional.of(column));

        // Act
        ColumnStatsResponse response = columnService.getColumnStats(columnId);

        // Assert
        assertNotNull(response);
        assertEquals(columnId, response.getColumnId());
        assertEquals(8, response.getTaskCount());
    }

    @Test
    void getColumnStats_DoneColumn_ReturnsStats() {
        // Arrange
        String columnId = "done";
        BoardColumn column = new BoardColumn();
        column.setId(columnId);
        column.setName("Done");
        column.setTaskCount(23);
        column.setLastUpdated(LocalDateTime.now());

        when(columnDataStore.findById(columnId)).thenReturn(Optional.of(column));

        // Act
        ColumnStatsResponse response = columnService.getColumnStats(columnId);

        // Assert
        assertNotNull(response);
        assertEquals(columnId, response.getColumnId());
        assertEquals(23, response.getTaskCount());
    }

    @Test
    void getColumnStats_ZeroTasks_ReturnsStats() {
        // Arrange
        String columnId = "to-do";
        BoardColumn column = new BoardColumn();
        column.setId(columnId);
        column.setName("To Do");
        column.setTaskCount(0);
        column.setLastUpdated(LocalDateTime.now());

        when(columnDataStore.findById(columnId)).thenReturn(Optional.of(column));

        // Act
        ColumnStatsResponse response = columnService.getColumnStats(columnId);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getTaskCount());
    }

    // ========== bulkUpdateColumnCounts Tests ==========

    @Test
    void bulkUpdateColumnCounts_ValidRequest_ReturnsResponse() {
        // Arrange
        BulkUpdateColumnCountsRequest.ColumnUpdate update1 = new BulkUpdateColumnCountsRequest.ColumnUpdate();
        update1.setColumnId("to-do");
        update1.setIncrement(-1);

        BulkUpdateColumnCountsRequest.ColumnUpdate update2 = new BulkUpdateColumnCountsRequest.ColumnUpdate();
        update2.setColumnId("in-progress");
        update2.setIncrement(1);

        BulkUpdateColumnCountsRequest request = new BulkUpdateColumnCountsRequest();
        request.setUpdates(Arrays.asList(update1, update2));

        BoardColumn column1 = new BoardColumn();
        column1.setId("to-do");
        column1.setTaskCount(10);

        BoardColumn column2 = new BoardColumn();
        column2.setId("in-progress");
        column2.setTaskCount(5);

        when(columnDataStore.findById("to-do")).thenReturn(Optional.of(column1));
        when(columnDataStore.findById("in-progress")).thenReturn(Optional.of(column2));
        doNothing().when(columnDataStore).incrementTaskCount(anyString(), anyInt());

        // Act
        BulkUpdateColumnCountsResponse response = columnService.bulkUpdateColumnCounts(request);

        // Assert
        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertEquals(2, response.getUpdatedColumns().size());
        assertTrue(response.getUpdatedColumns().contains("to-do"));
        assertTrue(response.getUpdatedColumns().contains("in-progress"));
        verify(columnDataStore).findById("to-do");
        verify(columnDataStore).findById("in-progress");
        verify(columnDataStore).incrementTaskCount("to-do", -1);
        verify(columnDataStore).incrementTaskCount("in-progress", 1);
    }

    @Test
    void bulkUpdateColumnCounts_SingleUpdate_ReturnsResponse() {
        // Arrange
        BulkUpdateColumnCountsRequest.ColumnUpdate update = new BulkUpdateColumnCountsRequest.ColumnUpdate();
        update.setColumnId("done");
        update.setIncrement(5);

        BulkUpdateColumnCountsRequest request = new BulkUpdateColumnCountsRequest();
        request.setUpdates(Collections.singletonList(update));

        BoardColumn column = new BoardColumn();
        column.setId("done");
        column.setTaskCount(20);

        when(columnDataStore.findById("done")).thenReturn(Optional.of(column));
        doNothing().when(columnDataStore).incrementTaskCount(anyString(), anyInt());

        // Act
        BulkUpdateColumnCountsResponse response = columnService.bulkUpdateColumnCounts(request);

        // Assert
        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertEquals(1, response.getUpdatedColumns().size());
        assertEquals("done", response.getUpdatedColumns().get(0));
        verify(columnDataStore).incrementTaskCount("done", 5);
    }

    @Test
    void bulkUpdateColumnCounts_ColumnNotFound_ThrowsException() {
        // Arrange
        BulkUpdateColumnCountsRequest.ColumnUpdate update = new BulkUpdateColumnCountsRequest.ColumnUpdate();
        update.setColumnId("invalid-column");
        update.setIncrement(1);

        BulkUpdateColumnCountsRequest request = new BulkUpdateColumnCountsRequest();
        request.setUpdates(Collections.singletonList(update));

        when(columnDataStore.findById("invalid-column")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ColumnNotFoundException.class, () -> {
            columnService.bulkUpdateColumnCounts(request);
        });
        verify(columnDataStore).findById("invalid-column");
        verify(columnDataStore, never()).incrementTaskCount(anyString(), anyInt());
    }

    @Test
    void bulkUpdateColumnCounts_MultipleColumns_ReturnsResponse() {
        // Arrange
        BulkUpdateColumnCountsRequest.ColumnUpdate update1 = new BulkUpdateColumnCountsRequest.ColumnUpdate();
        update1.setColumnId("to-do");
        update1.setIncrement(-2);

        BulkUpdateColumnCountsRequest.ColumnUpdate update2 = new BulkUpdateColumnCountsRequest.ColumnUpdate();
        update2.setColumnId("in-progress");
        update2.setIncrement(1);

        BulkUpdateColumnCountsRequest.ColumnUpdate update3 = new BulkUpdateColumnCountsRequest.ColumnUpdate();
        update3.setColumnId("done");
        update3.setIncrement(1);

        BulkUpdateColumnCountsRequest request = new BulkUpdateColumnCountsRequest();
        request.setUpdates(Arrays.asList(update1, update2, update3));

        BoardColumn column1 = new BoardColumn();
        column1.setId("to-do");
        BoardColumn column2 = new BoardColumn();
        column2.setId("in-progress");
        BoardColumn column3 = new BoardColumn();
        column3.setId("done");

        when(columnDataStore.findById("to-do")).thenReturn(Optional.of(column1));
        when(columnDataStore.findById("in-progress")).thenReturn(Optional.of(column2));
        when(columnDataStore.findById("done")).thenReturn(Optional.of(column3));
        doNothing().when(columnDataStore).incrementTaskCount(anyString(), anyInt());

        // Act
        BulkUpdateColumnCountsResponse response = columnService.bulkUpdateColumnCounts(request);

        // Assert
        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertEquals(3, response.getUpdatedColumns().size());
        verify(columnDataStore, times(3)).incrementTaskCount(anyString(), anyInt());
    }

    @Test
    void bulkUpdateColumnCounts_NegativeIncrement_ReturnsResponse() {
        // Arrange
        BulkUpdateColumnCountsRequest.ColumnUpdate update = new BulkUpdateColumnCountsRequest.ColumnUpdate();
        update.setColumnId("to-do");
        update.setIncrement(-3);

        BulkUpdateColumnCountsRequest request = new BulkUpdateColumnCountsRequest();
        request.setUpdates(Collections.singletonList(update));

        BoardColumn column = new BoardColumn();
        column.setId("to-do");
        column.setTaskCount(10);

        when(columnDataStore.findById("to-do")).thenReturn(Optional.of(column));
        doNothing().when(columnDataStore).incrementTaskCount(anyString(), anyInt());

        // Act
        BulkUpdateColumnCountsResponse response = columnService.bulkUpdateColumnCounts(request);

        // Assert
        assertNotNull(response);
        assertTrue(response.getSuccess());
        verify(columnDataStore).incrementTaskCount("to-do", -3);
    }

    // ========== updateColumnCounts Tests ==========

    @Test
    void updateColumnCounts_BothColumnsProvided_UpdatesBoth() {
        // Arrange
        String oldColumnId = "to-do";
        String newColumnId = "in-progress";
        doNothing().when(columnDataStore).incrementTaskCount(anyString(), anyInt());

        // Act
        columnService.updateColumnCounts(oldColumnId, newColumnId);

        // Assert
        verify(columnDataStore).incrementTaskCount(oldColumnId, -1);
        verify(columnDataStore).incrementTaskCount(newColumnId, 1);
    }

    @Test
    void updateColumnCounts_OnlyOldColumnProvided_UpdatesOldOnly() {
        // Arrange
        String oldColumnId = "to-do";
        doNothing().when(columnDataStore).incrementTaskCount(anyString(), anyInt());

        // Act
        columnService.updateColumnCounts(oldColumnId, null);

        // Assert
        verify(columnDataStore).incrementTaskCount(oldColumnId, -1);
        verify(columnDataStore, times(1)).incrementTaskCount(anyString(), anyInt());
    }

    @Test
    void updateColumnCounts_OnlyNewColumnProvided_UpdatesNewOnly() {
        // Arrange
        String newColumnId = "in-progress";
        doNothing().when(columnDataStore).incrementTaskCount(anyString(), anyInt());

        // Act
        columnService.updateColumnCounts(null, newColumnId);

        // Assert
        verify(columnDataStore).incrementTaskCount(newColumnId, 1);
        verify(columnDataStore, times(1)).incrementTaskCount(anyString(), anyInt());
    }

    @Test
    void updateColumnCounts_BothColumnsNull_DoesNothing() {
        // Arrange & Act
        columnService.updateColumnCounts(null, null);

        // Assert
        verify(columnDataStore, never()).incrementTaskCount(anyString(), anyInt());
    }
}