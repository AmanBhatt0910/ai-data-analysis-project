package com.ai.dashboard.backend.service;

import com.ai.dashboard.backend.dto.AggregationConfig;
import com.ai.dashboard.backend.dto.CsvData;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for aggregation operations on CSV data
 */
@Service
public class AggregationService {
    
    /**
     * Perform aggregations on grouped data
     * @param grouped map of grouped data
     * @param groupByFields fields used for grouping
     * @param aggregations list of aggregation configurations
     * @return list of aggregated results
     */
    public List<Map<String, Object>> aggregate(
            Map<String, List<CsvData>> grouped,
            List<String> groupByFields,
            List<AggregationConfig> aggregations) {
        
        return grouped.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> result = new LinkedHashMap<>();
                    
                    // Add grouping key fields
                    String[] keys = entry.getKey().split("\\|\\|");
                    for (int i = 0; i < groupByFields.size() && i < keys.length; i++) {
                        result.put(groupByFields.get(i), keys[i]);
                    }
                    
                    // Perform aggregations
                    if (aggregations != null) {
                        for (AggregationConfig agg : aggregations) {
                            String alias = agg.getAlias() != null ? agg.getAlias() : 
                                          agg.getType().toString() + "_" + agg.getField();
                            
                            Object aggValue = performAggregation(entry.getValue(), agg);
                            result.put(alias, aggValue);
                        }
                    }
                    
                    return result;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Perform a single aggregation operation
     */
    private Object performAggregation(List<CsvData> rows, AggregationConfig config) {
        return switch (config.getType()) {
            case SUM -> calculateSum(rows, config.getField());
            case AVG -> calculateAverage(rows, config.getField());
            case COUNT -> (long) rows.size();
            case MIN -> calculateMin(rows, config.getField());
            case MAX -> calculateMax(rows, config.getField());
            case DISTINCT_COUNT -> calculateDistinctCount(rows, config.getField());
        };
    }
    
    private double calculateSum(List<CsvData> rows, String field) {
        return rows.stream()
                .mapToDouble(row -> parseNumber(row.get(field)))
                .sum();
    }
    
    private double calculateAverage(List<CsvData> rows, String field) {
        return rows.stream()
                .mapToDouble(row -> parseNumber(row.get(field)))
                .average()
                .orElse(0.0);
    }
    
    private double calculateMin(List<CsvData> rows, String field) {
        return rows.stream()
                .mapToDouble(row -> parseNumber(row.get(field)))
                .min()
                .orElse(0.0);
    }
    
    private double calculateMax(List<CsvData> rows, String field) {
        return rows.stream()
                .mapToDouble(row -> parseNumber(row.get(field)))
                .max()
                .orElse(0.0);
    }
    
    private long calculateDistinctCount(List<CsvData> rows, String field) {
        return rows.stream()
                .map(row -> row.get(field))
                .filter(Objects::nonNull)
                .distinct()
                .count();
    }
    
    /**
     * Parse a value as a number
     */
    private double parseNumber(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
