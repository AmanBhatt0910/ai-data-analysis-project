package com.ai.dashboard.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

/**
 * Response for chart data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartDataResponse {
    
    private boolean success;
    private String message;
    private String chartType;
    private List<Map<String, Object>> chartData;
    private ChartConfig chartConfig;
    private String errorDetails;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartConfig {
        private String title;
        private String xAxisLabel;
        private String yAxisLabel;
        private Map<String, Object> additionalConfig;
    }
}
