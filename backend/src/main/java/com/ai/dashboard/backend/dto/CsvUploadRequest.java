package com.ai.dashboard.backend.dto;

import org.springframework.web.multipart.MultipartFile;

/**
 * Request object for CSV file upload
 */
public class CsvUploadRequest {
    private MultipartFile file;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
