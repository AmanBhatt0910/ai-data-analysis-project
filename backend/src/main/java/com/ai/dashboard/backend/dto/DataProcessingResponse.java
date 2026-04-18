package com.ai.dashboard.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

/**
 * Response from data processing operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataProcessingResponse {
    
    private boolean success;
    private String message;
    private List<Map<String, Object>> data;
    private List<String> columns;
    private int totalRows;
    private long processingTimeMs;
    private String errorDetails;
    private Map<String, Object> metadata;
}
