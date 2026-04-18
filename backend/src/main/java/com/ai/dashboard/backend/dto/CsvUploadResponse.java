package com.ai.dashboard.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CsvUploadResponse {
    private boolean success;
    private String message;
    private List<String> headers;
    private List<CsvData> data;
    private int rowCount;
    private String errorDetails;
}
