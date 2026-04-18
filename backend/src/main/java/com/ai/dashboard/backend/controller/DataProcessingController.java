package com.ai.dashboard.backend.controller;

import com.ai.dashboard.backend.dto.*;
import com.ai.dashboard.backend.service.DataProcessingService;
import com.ai.dashboard.backend.service.DataStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST API controller for data processing operations
 */
@RestController
@RequestMapping("/api/data-processing")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class DataProcessingController {
    
    private final DataProcessingService dataProcessingService;
    private final DataStorageService dataStorageService;
    
    public DataProcessingController(DataProcessingService dataProcessingService,
                                   DataStorageService dataStorageService) {
        this.dataProcessingService = dataProcessingService;
        this.dataStorageService = dataStorageService;
    }
    
    /**
     * Store uploaded CSV data and return session ID
     */
    @PostMapping("/store-csv")
    public ResponseEntity<Map<String, String>> storeCsvData(@RequestBody List<CsvData> csvData) {
        try {
            if (csvData == null || csvData.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "No CSV data provided"));
            }
            
            String sessionId = UUID.randomUUID().toString();
            dataStorageService.storeData(sessionId, csvData);
            
            return ResponseEntity.ok(Map.of(
                    "sessionId", sessionId,
                    "rowCount", String.valueOf(csvData.size())
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to store CSV data: " + e.getMessage()));
        }
    }
    
    /**
     * Process data with filtering, grouping, and aggregation
     */
    @PostMapping("/process")
    public ResponseEntity<DataProcessingResponse> processData(
            @RequestParam String sessionId,
            @RequestBody DataProcessingRequest request) {
        try {
            // Retrieve stored data
            if (!dataStorageService.hasData(sessionId)) {
                return ResponseEntity.badRequest()
                        .body(DataProcessingResponse.builder()
                                .success(false)
                                .message("Session not found")
                                .build());
            }
            
            List<CsvData> data = dataStorageService.retrieveData(sessionId);
            
            // Process the data
            DataProcessingResponse response = dataProcessingService.processData(data, request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DataProcessingResponse.builder()
                            .success(false)
                            .message("Error processing data")
                            .errorDetails(e.getMessage())
                            .build());
        }
    }
    
    /**
     * Get chart data from processed results
     */
    @PostMapping("/chart-data")
    public ResponseEntity<ChartDataResponse> getChartData(
            @RequestParam String sessionId,
            @RequestParam(defaultValue = "bar") String chartType,
            @RequestParam String xAxisField,
            @RequestParam String yAxisField,
            @RequestBody(required = false) DataProcessingRequest request) {
        try {
            if (!dataStorageService.hasData(sessionId)) {
                return ResponseEntity.badRequest()
                        .body(ChartDataResponse.builder()
                                .success(false)
                                .message("Session not found")
                                .build());
            }
            
            List<CsvData> data = dataStorageService.retrieveData(sessionId);
            
            // Process data if request is provided
            DataProcessingResponse processedResponse;
            if (request != null) {
                processedResponse = dataProcessingService.processData(data, request);
            } else {
                // Use raw data
                DataProcessingResponse rawResponse = dataProcessingService.processData(
                        data,
                        DataProcessingRequest.builder().build()
                );
                processedResponse = rawResponse;
            }
            
            if (!processedResponse.isSuccess()) {
                return ResponseEntity.badRequest()
                        .body(ChartDataResponse.builder()
                                .success(false)
                                .message("Failed to process data for chart")
                                .build());
            }
            
            // Get chart data from processed results
            ChartDataResponse chartResponse = dataProcessingService.getChartData(
                    processedResponse.getData(),
                    chartType,
                    xAxisField,
                    yAxisField
            );
            
            return ResponseEntity.ok(chartResponse);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ChartDataResponse.builder()
                            .success(false)
                            .message("Error preparing chart data")
                            .errorDetails(e.getMessage())
                            .build());
        }
    }
    
    /**
     * Get aggregation summary (quick stats)
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary(
            @RequestParam String sessionId) {
        try {
            if (!dataStorageService.hasData(sessionId)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Session not found"));
            }
            
            List<CsvData> data = dataStorageService.retrieveData(sessionId);
            
            return ResponseEntity.ok(Map.of(
                    "totalRows", data.size(),
                    "columns", data.isEmpty() ? 0 : data.get(0).getData().size()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error getting summary: " + e.getMessage()));
        }
    }
    
    /**
     * Clear data for a session
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, String>> clearData(
            @RequestParam String sessionId) {
        try {
            dataStorageService.clearData(sessionId);
            return ResponseEntity.ok(Map.of("message", "Data cleared successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error clearing data: " + e.getMessage()));
        }
    }
    
    /**
     * Get list of available columns for a dataset
     */
    @GetMapping("/columns")
    public ResponseEntity<Map<String, Object>> getColumns(
            @RequestParam String sessionId) {
        try {
            if (!dataStorageService.hasData(sessionId)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Session not found"));
            }
            
            List<CsvData> data = dataStorageService.retrieveData(sessionId);
            
            if (data.isEmpty()) {
                return ResponseEntity.ok(Map.of("columns", List.of()));
            }
            
            List<String> columns = data.get(0).getData().keySet().stream().toList();
            
            return ResponseEntity.ok(Map.of("columns", columns));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error retrieving columns: " + e.getMessage()));
        }
    }
}
