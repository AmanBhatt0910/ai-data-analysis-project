package com.ai.dashboard.backend.service;

import com.ai.dashboard.backend.dto.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main service for orchestrating data processing operations
 */
@Service
public class DataProcessingService {
    
    private final FilterService filterService;
    private final GroupingService groupingService;
    private final AggregationService aggregationService;
    
    public DataProcessingService(FilterService filterService,
                                GroupingService groupingService,
                                AggregationService aggregationService) {
        this.filterService = filterService;
        this.groupingService = groupingService;
        this.aggregationService = aggregationService;
    }
    
    /**
     * Process data with filtering, grouping, and aggregation
     */
    public DataProcessingResponse processData(List<CsvData> data, DataProcessingRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            if (data == null || data.isEmpty()) {
                return DataProcessingResponse.builder()
                        .success(false)
                        .message("No data available")
                        .build();
            }
            
            // Step 1: Apply filters
            List<CsvData> filteredData = filterService.filter(data, request.getFilters());
            
            if (filteredData.isEmpty()) {
                return DataProcessingResponse.builder()
                        .success(true)
                        .message("No data matches the filters")
                        .data(new ArrayList<>())
                        .columns(new ArrayList<>())
                        .totalRows(0)
                        .processingTimeMs(System.currentTimeMillis() - startTime)
                        .build();
            }
            
            // Step 2: Apply grouping and aggregation
            List<Map<String, Object>> processedData;
            
            if (request.getGroupByFields() != null && !request.getGroupByFields().isEmpty()) {
                // Group data
                Map<String, List<CsvData>> grouped = groupingService.groupByFieldsInternal(
                        filteredData, request.getGroupByFields());
                
                // Apply aggregations if provided
                if (request.getAggregations() != null && !request.getAggregations().isEmpty()) {
                    processedData = aggregationService.aggregate(
                            grouped, request.getGroupByFields(), request.getAggregations());
                } else {
                    processedData = groupingService.groupData(filteredData, request.getGroupByFields());
                }
            } else if (request.getAggregations() != null && !request.getAggregations().isEmpty()) {
                // Perform aggregations without grouping
                Map<String, List<CsvData>> grouped = new HashMap<>();
                grouped.put("total", filteredData);
                processedData = aggregationService.aggregate(grouped, new ArrayList<>(), request.getAggregations());
            } else {
                // Return filtered data as is
                processedData = filteredData.stream()
                        .map(CsvData::getData)
                        .map(d -> {
                            Map<String, Object> result = new LinkedHashMap<>();
                            d.forEach((k, v) -> result.put(k, v));
                            return result;
                        })
                        .collect(Collectors.toList());
            }
            
            // Step 3: Apply sorting
            processedData = applySorting(processedData, request.getSortBy(), request.getSortOrder());
            
            // Step 4: Apply pagination
            int offset = request.getOffset() != null ? request.getOffset() : 0;
            int limit = request.getLimit() != null ? request.getLimit() : Integer.MAX_VALUE;
            
            List<Map<String, Object>> paginatedData = processedData.stream()
                    .skip(offset)
                    .limit(limit)
                    .collect(Collectors.toList());
            
            // Extract column names
            Set<String> columnSet = new LinkedHashSet<>();
            if (!paginatedData.isEmpty()) {
                columnSet.addAll(paginatedData.get(0).keySet());
            }
            
            return DataProcessingResponse.builder()
                    .success(true)
                    .message("Data processed successfully")
                    .data(paginatedData)
                    .columns(new ArrayList<>(columnSet))
                    .totalRows(processedData.size())
                    .processingTimeMs(System.currentTimeMillis() - startTime)
                    .build();
            
        } catch (Exception e) {
            return DataProcessingResponse.builder()
                    .success(false)
                    .message("Error processing data")
                    .errorDetails(e.getMessage())
                    .processingTimeMs(System.currentTimeMillis() - startTime)
                    .build();
        }
    }
    
    /**
     * Apply sorting to processed data
     */
    private List<Map<String, Object>> applySorting(List<Map<String, Object>> data,
                                                    String sortBy,
                                                    DataProcessingRequest.SortOrder sortOrder) {
        if (sortBy == null || sortBy.isEmpty()) {
            return data;
        }
        
        boolean ascending = sortOrder == null || sortOrder == DataProcessingRequest.SortOrder.ASC;
        
        return data.stream()
                .sorted((a, b) -> {
                    Object aVal = a.get(sortBy);
                    Object bVal = b.get(sortBy);
                    
                    if (aVal == null && bVal == null) return 0;
                    if (aVal == null) return ascending ? -1 : 1;
                    if (bVal == null) return ascending ? 1 : -1;
                    
                    int comparison = compareValues(aVal, bVal);
                    return ascending ? comparison : -comparison;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Compare two values for sorting
     */
    private int compareValues(Object a, Object b) {
        if (a instanceof Number && b instanceof Number) {
            return Double.compare(((Number) a).doubleValue(), ((Number) b).doubleValue());
        }
        return a.toString().compareTo(b.toString());
    }
    
    /**
     * Get chart-compatible data from processed results
     */
    public ChartDataResponse getChartData(List<Map<String, Object>> processedData,
                                         String chartType,
                                         String xAxisField,
                                         String yAxisField) {
        try {
            if (processedData == null || processedData.isEmpty()) {
                return ChartDataResponse.builder()
                        .success(false)
                        .message("No data available for chart")
                        .build();
            }
            
            List<Map<String, Object>> chartData = processedData.stream()
                    .map(row -> {
                        Map<String, Object> chartRow = new LinkedHashMap<>();
                        chartRow.put(xAxisField, row.get(xAxisField));
                        chartRow.put(yAxisField, row.get(yAxisField));
                        return chartRow;
                    })
                    .collect(Collectors.toList());
            
            ChartDataResponse.ChartConfig config = ChartDataResponse.ChartConfig.builder()
                    .title(chartType + " Chart - " + yAxisField + " by " + xAxisField)
                    .xAxisLabel(xAxisField)
                    .yAxisLabel(yAxisField)
                    .build();
            
            return ChartDataResponse.builder()
                    .success(true)
                    .message("Chart data prepared successfully")
                    .chartType(chartType)
                    .chartData(chartData)
                    .chartConfig(config)
                    .build();
            
        } catch (Exception e) {
            return ChartDataResponse.builder()
                    .success(false)
                    .message("Error preparing chart data")
                    .errorDetails(e.getMessage())
                    .build();
        }
    }
}
