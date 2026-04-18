package com.ai.dashboard.backend.service;

import com.ai.dashboard.backend.dto.CsvData;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service to store and manage uploaded CSV data in memory
 * Uses a simple in-memory storage with session-based data management
 */
@Service
public class DataStorageService {
    
    // Store data by session ID
    private final Map<String, List<CsvData>> dataStore = new ConcurrentHashMap<>();
    
    /**
     * Store CSV data
     * @param sessionId unique identifier for the data session
     * @param data list of CSV data rows
     */
    public void storeData(String sessionId, List<CsvData> data) {
        dataStore.put(sessionId, new ArrayList<>(data));
    }
    
    /**
     * Retrieve stored data
     * @param sessionId unique identifier for the data session
     * @return list of CSV data rows
     */
    public List<CsvData> retrieveData(String sessionId) {
        return dataStore.getOrDefault(sessionId, new ArrayList<>());
    }
    
    /**
     * Check if data exists for session
     * @param sessionId unique identifier for the data session
     * @return true if data exists
     */
    public boolean hasData(String sessionId) {
        return dataStore.containsKey(sessionId);
    }
    
    /**
     * Clear data for a session
     * @param sessionId unique identifier for the data session
     */
    public void clearData(String sessionId) {
        dataStore.remove(sessionId);
    }
    
    /**
     * Clear all stored data
     */
    public void clearAllData() {
        dataStore.clear();
    }
    
    /**
     * Get all available sessions
     * @return list of session IDs
     */
    public List<String> getAllSessions() {
        return new ArrayList<>(dataStore.keySet());
    }
}
