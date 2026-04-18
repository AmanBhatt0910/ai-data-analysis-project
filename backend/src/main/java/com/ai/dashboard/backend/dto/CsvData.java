package com.ai.dashboard.backend.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.util.HashMap;
import java.util.Map;

/**
 * Generic data structure to handle CSV rows with dynamic columns
 */
public class CsvData {
    private Map<String, String> data = new HashMap<>();

    @JsonAnySetter
    public void setData(String key, String value) {
        this.data.put(key, value);
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public String get(String key) {
        return data.get(key);
    }

    public void put(String key, String value) {
        data.put(key, value);
    }
}
