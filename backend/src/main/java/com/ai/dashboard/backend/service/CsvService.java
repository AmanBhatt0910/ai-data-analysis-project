package com.ai.dashboard.backend.service;

import com.ai.dashboard.backend.dto.CsvData;
import com.ai.dashboard.backend.dto.CsvUploadResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class CsvService {

    /**
     * Parse CSV file and return structured data with dynamic columns
     * @param file the uploaded CSV file
     * @return CsvUploadResponse with parsed data and metadata
     */
    public CsvUploadResponse parseCsv(MultipartFile file) {
        try {
            // Validate file
            if (file == null || file.isEmpty()) {
                return CsvUploadResponse.builder()
                        .success(false)
                        .message("No file provided")
                        .build();
            }

            // Validate file type
            String fileName = file.getOriginalFilename();
            if (fileName == null || !fileName.toLowerCase().endsWith(".csv")) {
                return CsvUploadResponse.builder()
                        .success(false)
                        .message("Invalid file type. Only CSV files are allowed")
                        .errorDetails("File must have .csv extension")
                        .build();
            }

            // Parse CSV content
            List<String> headers = new ArrayList<>();
            List<CsvData> data = new ArrayList<>();

            try (InputStream inputStream = file.getInputStream();
                 InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                 CSVParser csvParser = CSVFormat.DEFAULT
                         .withFirstRecordAsHeader()
                         .withTrim()
                         .withIgnoreSurroundingSpaces()
                         .parse(streamReader)) {

                // Get headers
                headers.addAll(csvParser.getHeaderMap().keySet());

                if (headers.isEmpty()) {
                    return CsvUploadResponse.builder()
                            .success(false)
                            .message("CSV file has no headers")
                            .build();
                }

                // Parse records
                for (CSVRecord record : csvParser) {
                    CsvData row = new CsvData();
                    for (String header : headers) {
                        String value = record.get(header);
                        row.put(header, value != null ? value.trim() : "");
                    }
                    data.add(row);
                }

                if (data.isEmpty()) {
                    return CsvUploadResponse.builder()
                            .success(false)
                            .message("CSV file contains no data rows")
                            .build();
                }

                return CsvUploadResponse.builder()
                        .success(true)
                        .message("CSV file parsed successfully")
                        .headers(headers)
                        .data(data)
                        .rowCount(data.size())
                        .build();

            } catch (IOException e) {
                return CsvUploadResponse.builder()
                        .success(false)
                        .message("Error reading CSV file")
                        .errorDetails(e.getMessage())
                        .build();
            }

        } catch (Exception e) {
            return CsvUploadResponse.builder()
                    .success(false)
                    .message("Unexpected error while parsing CSV")
                    .errorDetails(e.getMessage())
                    .build();
        }
    }

    /**
     * Validate CSV data structure
     * @param csvResponse the parsed CSV response
     * @return true if valid, false otherwise
     */
    public boolean validateCsvData(CsvUploadResponse csvResponse) {
        if (csvResponse == null || !csvResponse.isSuccess()) {
            return false;
        }

        if (csvResponse.getHeaders() == null || csvResponse.getHeaders().isEmpty()) {
            return false;
        }

        if (csvResponse.getData() == null || csvResponse.getData().isEmpty()) {
            return false;
        }

        // Validate each row has all columns
        for (CsvData row : csvResponse.getData()) {
            if (row.getData().size() != csvResponse.getHeaders().size()) {
                return false;
            }
        }

        return true;
    }
}
