package com.ai.dashboard.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a filter criterion for data filtering
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterCriteria {
    
    public enum Operator {
        EQUALS,
        NOT_EQUALS,
        CONTAINS,
        NOT_CONTAINS,
        GREATER_THAN,
        LESS_THAN,
        GREATER_THAN_OR_EQUAL,
        LESS_THAN_OR_EQUAL,
        IN,
        NOT_IN,
        STARTS_WITH,
        ENDS_WITH
    }
    
    @JsonProperty("field")
    private String field;
    
    @JsonProperty("operator")
    private Operator operator;
    
    @JsonProperty("value")
    private Object value;
    
    @JsonProperty("caseSensitive")
    @Builder.Default
    private boolean caseSensitive = false;
}
