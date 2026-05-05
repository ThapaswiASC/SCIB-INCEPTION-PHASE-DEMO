package com.myproject.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.exceptions.ColumnNotFoundException;
import com.myproject.models.dtos.BulkUpdateColumnCountsRequest;
import com.myproject.models.dtos.BulkUpdateColumnCountsResponse;
import com.myproject.models.dtos.ColumnStatsResponse;
import com.myproject.services.interfaces.ColumnService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ColumnController.class)
class ColumnControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ColumnService columnService;

    // ========== getColumnStats Tests ==========

    @Test
    void getColumnStats_ValidColumnId_ReturnsOk() throws Exception {
        // Arrange
        String columnId = "to-do";
        ColumnStatsResponse response = new ColumnStatsResponse();
        response.setColumnId(columnId);
        response.setTaskCount(15);
        response.setLastUpdated(LocalDateTime.now());

        when(columnService.getColumnStats(columnId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/v1/columns/{columnId}/stats", columnId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.columnId").value(columnId))
            .andExpect(jsonPath("$.taskCount").value(15))
            .andExpect(jsonPath("$.lastUpdated").exists());

        verify(columnService, times(1)).getColumnStats(columnId);
    }

    @Test
    void getColumnStats_ColumnNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        String columnId = "invalid-column";
        when(columnService.getColumnStats(columnId))
            .thenThrow(new ColumnNotFoundException(columnId));

        // Act & Assert
        mockMvc.perform(get("/v1/columns/{columnId}/stats", columnId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode").value("COLUMN_NOT_FOUND"))
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getColumnStats_InProgressColumn_ReturnsOk() throws Exception {
        // Arrange
        String columnId = "in-progress";
        ColumnStatsResponse response = new ColumnStatsResponse();
        response.setColumnId(columnId);
        response.setTaskCount(8);
        response.setLastUpdated(LocalDateTime.now());

        when(columnService.getColumnStats(columnId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/v1/columns/{columnId}/stats", columnId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.columnId").value(columnId))
            .andExpect(jsonPath("$.taskCount").value(8));
    }

    @Test
    void getColumnStats_DoneColumn_ReturnsOk() throws Exception {
        // Arrange
        String columnId = "done";
        ColumnStatsResponse response = new ColumnStatsResponse();
        response.setColumnId(columnId);
        response.setTaskCount(23);
        response.setLastUpdated(LocalDateTime.now());

        when(columnService.getColumnStats(columnId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/v1/columns/{columnId}/stats", columnId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.columnId").value(columnId))
            .andExpect(jsonPath("$.taskCount").value(23));
    }

    @Test
    void getColumnStats_ZeroTasks_ReturnsOk() throws Exception {
        // Arrange
        String columnId = "to-do";
        ColumnStatsResponse response = new ColumnStatsResponse();
        response.setColumnId(columnId);
        response.setTaskCount(0);
        response.setLastUpdated(LocalDateTime.now());

        when(columnService.getColumnStats(columnId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/v1/columns/{columnId}/stats", columnId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.taskCount").value(0));
    }

    // ========== bulkUpdateColumnCounts Tests ==========

    @Test
    void bulkUpdateColumnCounts_ValidRequest_ReturnsOk() throws Exception {
        // Arrange
        BulkUpdateColumnCountsRequest.ColumnUpdate update1 = new BulkUpdateColumnCountsRequest.ColumnUpdate();
        update1.setColumnId("to-do");
        update1.setIncrement(-1);

        BulkUpdateColumnCountsRequest.ColumnUpdate update2 = new BulkUpdateColumnCountsRequest.ColumnUpdate();
        update2.setColumnId("in-progress");
        update2.setIncrement(1);

        BulkUpdateColumnCountsRequest request = new BulkUpdateColumnCountsRequest();
        request.setUpdates(Arrays.asList(update1, update2));

        BulkUpdateColumnCountsResponse response = new BulkUpdateColumnCountsResponse();
        response.setSuccess(true);
        response.setUpdatedColumns(Arrays.asList("to-do", "in-progress"));

        when(columnService.bulkUpdateColumnCounts(any(BulkUpdateColumnCountsRequest.class)))
            .thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/v1/columns/bulk-update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.updatedColumns").isArray())
            .andExpect(jsonPath("$.updatedColumns.length()").value(2))
            .andExpect(jsonPath("$.updatedColumns[0]").value("to-do"))
            .andExpect(jsonPath("$.updatedColumns[1]").value("in-progress"));

        verify(columnService, times(1)).bulkUpdateColumnCounts(any(BulkUpdateColumnCountsRequest.class));
    }

    @Test
    void bulkUpdateColumnCounts_SingleUpdate_ReturnsOk() throws Exception {
        // Arrange
        BulkUpdateColumnCountsRequest.ColumnUpdate update = new BulkUpdateColumnCountsRequest.ColumnUpdate();
        update.setColumnId("done");
        update.setIncrement(5);

        BulkUpdateColumnCountsRequest request = new BulkUpdateColumnCountsRequest();
        request.setUpdates(Collections.singletonList(update));

        BulkUpdateColumnCountsResponse response = new BulkUpdateColumnCountsResponse();
        response.setSuccess(true);
        response.setUpdatedColumns(Collections.singletonList("done"));

        when(columnService.bulkUpdateColumnCounts(any(BulkUpdateColumnCountsRequest.class)))
            .thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/v1/columns/bulk-update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.updatedColumns.length()").value(1));
    }

    @Test
    void bulkUpdateColumnCounts_MultipleColumns_ReturnsOk() throws Exception {
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

        BulkUpdateColumnCountsResponse response = new BulkUpdateColumnCountsResponse();
        response.setSuccess(true);
        response.setUpdatedColumns(Arrays.asList("to-do", "in-progress", "done"));

        when(columnService.bulkUpdateColumnCounts(any(BulkUpdateColumnCountsRequest.class)))
            .thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/v1/columns/bulk-update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.updatedColumns.length()").value(3));
    }

    @Test
    void bulkUpdateColumnCounts_ColumnNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        BulkUpdateColumnCountsRequest.ColumnUpdate update = new BulkUpdateColumnCountsRequest.ColumnUpdate();
        update.setColumnId("invalid-column");
        update.setIncrement(1);

        BulkUpdateColumnCountsRequest request = new BulkUpdateColumnCountsRequest();
        request.setUpdates(Collections.singletonList(update));

        when(columnService.bulkUpdateColumnCounts(any(BulkUpdateColumnCountsRequest.class)))
            .thenThrow(new ColumnNotFoundException("invalid-column"));

        // Act & Assert
        mockMvc.perform(put("/v1/columns/bulk-update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode").value("COLUMN_NOT_FOUND"));
    }

    @Test
    void bulkUpdateColumnCounts_MissingUpdates_ReturnsBadRequest() throws Exception {
        // Arrange
        BulkUpdateColumnCountsRequest request = new BulkUpdateColumnCountsRequest();
        // updates is null

        // Act & Assert
        mockMvc.perform(put("/v1/columns/bulk-update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    void bulkUpdateColumnCounts_MissingColumnId_ReturnsBadRequest() throws Exception {
        // Arrange
        BulkUpdateColumnCountsRequest.ColumnUpdate update = new BulkUpdateColumnCountsRequest.ColumnUpdate();
        update.setIncrement(1);
        // columnId is null

        BulkUpdateColumnCountsRequest request = new BulkUpdateColumnCountsRequest();
        request.setUpdates(Collections.singletonList(update));

        // Act & Assert
        mockMvc.perform(put("/v1/columns/bulk-update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    void bulkUpdateColumnCounts_MissingIncrement_ReturnsBadRequest() throws Exception {
        // Arrange
        BulkUpdateColumnCountsRequest.ColumnUpdate update = new BulkUpdateColumnCountsRequest.ColumnUpdate();
        update.setColumnId("to-do");
        // increment is null

        BulkUpdateColumnCountsRequest request = new BulkUpdateColumnCountsRequest();
        request.setUpdates(Collections.singletonList(update));

        // Act & Assert
        mockMvc.perform(put("/v1/columns/bulk-update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    void bulkUpdateColumnCounts_NegativeIncrement_ReturnsOk() throws Exception {
        // Arrange
        BulkUpdateColumnCountsRequest.ColumnUpdate update = new BulkUpdateColumnCountsRequest.ColumnUpdate();
        update.setColumnId("to-do");
        update.setIncrement(-3);

        BulkUpdateColumnCountsRequest request = new BulkUpdateColumnCountsRequest();
        request.setUpdates(Collections.singletonList(update));

        BulkUpdateColumnCountsResponse response = new BulkUpdateColumnCountsResponse();
        response.setSuccess(true);
        response.setUpdatedColumns(Collections.singletonList("to-do"));

        when(columnService.bulkUpdateColumnCounts(any(BulkUpdateColumnCountsRequest.class)))
            .thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/v1/columns/bulk-update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }
}