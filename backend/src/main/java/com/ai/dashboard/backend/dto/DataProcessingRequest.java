package com.ai.dashboard.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Request for data processing operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataProcessingRequest {
    
    @JsonProperty("filters")
    private List<FilterCriteria> filters;
    
    @JsonProperty("groupByFields")
    private List<String> groupByFields;
    
    @JsonProperty("aggregations")
    private List<AggregationConfig> aggregations;
    
    @JsonProperty("sortBy")
    private String sortBy;
    
    @JsonProperty("sortOrder")
    private SortOrder sortOrder;
    
    @JsonProperty("limit")
    private Integer limit;
    
    @JsonProperty("offset")
    private Integer offset;
    
    public enum SortOrder {
        ASC,
        DESC
    }
}
