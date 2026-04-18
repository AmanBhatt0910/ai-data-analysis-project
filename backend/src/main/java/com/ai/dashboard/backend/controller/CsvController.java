package com.ai.dashboard.backend.controller;

import com.ai.dashboard.backend.dto.CsvUploadResponse;
import com.ai.dashboard.backend.service.CsvService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST API controller for CSV file upload and processing
 */
@RestController
@RequestMapping("/api/csv")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class CsvController {

    private final CsvService csvService;

    public CsvController(CsvService csvService) {
        this.csvService = csvService;
    }

    /**
     * Upload and parse a CSV file
     * @param file the CSV file to upload
     * @return Response with parsed CSV data or error message
     */
    @PostMapping("/upload")
    public ResponseEntity<CsvUploadResponse> uploadCsv(@RequestParam("file") MultipartFile file) {
        try {
            // Parse the CSV file
            CsvUploadResponse response = csvService.parseCsv(file);

            if (!response.isSuccess()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Validate the parsed data
            if (!csvService.validateCsvData(response)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(CsvUploadResponse.builder()
                                .success(false)
                                .message("CSV data validation failed")
                                .errorDetails("Invalid CSV structure")
                                .build());
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CsvUploadResponse.builder()
                            .success(false)
                            .message("Server error while processing CSV")
                            .errorDetails(e.getMessage())
                            .build());
        }
    }

    /**
     * Health check endpoint
     * @return Status message
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("CSV upload service is running");
    }
}
