package com.myproject.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.utils.PerformanceMonitor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HealthController.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PerformanceMonitor performanceMonitor;

    // ========== GET /v1/health - Health Check ==========

    @Test
    void getHealth_ReturnsHealthStatus() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/v1/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"))
            .andExpect(jsonPath("$.application").value("myproject"))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.timestamp").isNumber());

        verifyNoInteractions(performanceMonitor);
    }

    @Test
    void getHealth_TimestampIsRecent() throws Exception {
        // Arrange
        long beforeRequest = System.currentTimeMillis();

        // Act
        mockMvc.perform(get("/v1/health"))
            .andExpect(status().isOk())
            .andExpect(result -> {
                String responseBody = result.getResponse().getContentAsString();
                long timestamp = objectMapper.readTree(responseBody).get("timestamp").asLong();
                long afterRequest = System.currentTimeMillis();
                
                // Timestamp should be between before and after request
                assert timestamp >= beforeRequest && timestamp <= afterRequest;
            });
    }

    // ========== GET /v1/health/metrics - Performance Metrics ==========

    @Test
    void getMetrics_ReturnsPerformanceMetrics() throws Exception {
        // Arrange
        when(performanceMonitor.getTotalTasksCreated()).thenReturn(1000L);
        when(performanceMonitor.getAverageCreationTime()).thenReturn(150L);

        // Act & Assert
        mockMvc.perform(get("/v1/health/metrics"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalTasksCreated").value(1000))
            .andExpect(jsonPath("$.averageCreationTimeMs").value(150))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.timestamp").isNumber());

        verify(performanceMonitor, times(1)).getTotalTasksCreated();
        verify(performanceMonitor, times(1)).getAverageCreationTime();
    }

    @Test
    void getMetrics_NoTasksCreated_ReturnsZeroMetrics() throws Exception {
        // Arrange
        when(performanceMonitor.getTotalTasksCreated()).thenReturn(0L);
        when(performanceMonitor.getAverageCreationTime()).thenReturn(0L);

        // Act & Assert
        mockMvc.perform(get("/v1/health/metrics"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalTasksCreated").value(0))
            .andExpect(jsonPath("$.averageCreationTimeMs").value(0))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(performanceMonitor, times(1)).getTotalTasksCreated();
        verify(performanceMonitor, times(1)).getAverageCreationTime();
    }

    @Test
    void getMetrics_HighTaskVolume_ReturnsCorrectMetrics() throws Exception {
        // Arrange
        when(performanceMonitor.getTotalTasksCreated()).thenReturn(50000L);
        when(performanceMonitor.getAverageCreationTime()).thenReturn(85L);

        // Act & Assert
        mockMvc.perform(get("/v1/health/metrics"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalTasksCreated").value(50000))
            .andExpect(jsonPath("$.averageCreationTimeMs").value(85));

        verify(performanceMonitor, times(1)).getTotalTasksCreated();
        verify(performanceMonitor, times(1)).getAverageCreationTime();
    }

    @Test
    void getMetrics_TimestampIsRecent() throws Exception {
        // Arrange
        when(performanceMonitor.getTotalTasksCreated()).thenReturn(100L);
        when(performanceMonitor.getAverageCreationTime()).thenReturn(120L);
        long beforeRequest = System.currentTimeMillis();

        // Act
        mockMvc.perform(get("/v1/health/metrics"))
            .andExpect(status().isOk())
            .andExpect(result -> {
                String responseBody = result.getResponse().getContentAsString();
                long timestamp = objectMapper.readTree(responseBody).get("timestamp").asLong();
                long afterRequest = System.currentTimeMillis();
                
                // Timestamp should be between before and after request
                assert timestamp >= beforeRequest && timestamp <= afterRequest;
            });
    }
}