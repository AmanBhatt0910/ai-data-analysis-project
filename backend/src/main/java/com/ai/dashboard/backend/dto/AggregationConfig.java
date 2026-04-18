package com.ai.dashboard.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Configuration for aggregation operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AggregationConfig {
    
    public enum AggregationType {
        SUM,
        AVG,
        COUNT,
        MIN,
        MAX,
        DISTINCT_COUNT
    }
    
    @JsonProperty("field")
    private String field;
    
    @JsonProperty("type")
    private AggregationType type;
    
    @JsonProperty("alias")
    private String alias;
}
