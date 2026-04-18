package com.ai.dashboard.backend.service;

import com.ai.dashboard.backend.dto.CsvData;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for grouping operations on CSV data
 */
@Service
public class GroupingService {
    
    /**
     * Group data by specified fields
     * @param data list of CSV data rows
     * @param groupByFields fields to group by
     * @return grouped data as list of maps
     */
    public List<Map<String, Object>> groupData(List<CsvData> data, List<String> groupByFields) {
        if (groupByFields == null || groupByFields.isEmpty()) {
            return data.stream()
                    .map(CsvData::getData)
                    .map(d -> {
                        Map<String, Object> result = new LinkedHashMap<>();
                        d.forEach((k, v) -> result.put(k, v));
                        return result;
                    })
                    .collect(Collectors.toList());
        }
        
        // Create grouped map
        Map<String, List<CsvData>> grouped = groupByFieldsInternal(data, groupByFields);
        
        // Convert grouped data to list of maps
        return grouped.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    
                    // Add grouping key fields
                    String[] keys = entry.getKey().split("\\|\\|");
                    for (int i = 0; i < groupByFields.size() && i < keys.length; i++) {
                        row.put(groupByFields.get(i), keys[i]);
                    }
                    
                    // Add count of grouped items
                    row.put("__group_count", entry.getValue().size());
                    
                    return row;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Group data internally for aggregation
     */
    public Map<String, List<CsvData>> groupByFieldsInternal(List<CsvData> data, List<String> groupByFields) {
        return data.stream()
                .collect(Collectors.groupingBy(row -> buildGroupKey(row, groupByFields)));
    }
    
    /**
     * Build a composite key for grouping
     */
    private String buildGroupKey(CsvData row, List<String> groupByFields) {
        return groupByFields.stream()
                .map(field -> row.get(field) != null ? row.get(field) : "")
                .collect(Collectors.joining("||"));
    }
}
