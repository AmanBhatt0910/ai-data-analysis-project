package com.ai.dashboard.backend.service;

import com.ai.dashboard.backend.dto.CsvData;
import com.ai.dashboard.backend.dto.FilterCriteria;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for filtering operations on CSV data
 */
@Service
public class FilterService {
    
    /**
     * Apply filters to data
     * @param data list of CSV data rows
     * @param filters list of filter criteria
     * @return filtered data
     */
    public List<CsvData> filter(List<CsvData> data, List<FilterCriteria> filters) {
        if (filters == null || filters.isEmpty()) {
            return new ArrayList<>(data);
        }
        
        return data.stream()
                .filter(row -> matchesAllFilters(row, filters))
                .collect(Collectors.toList());
    }
    
    /**
     * Check if a row matches all filter criteria
     */
    private boolean matchesAllFilters(CsvData row, List<FilterCriteria> filters) {
        return filters.stream()
                .allMatch(filter -> matchesFilter(row, filter));
    }
    
    /**
     * Check if a row matches a specific filter criterion
     */
    private boolean matchesFilter(CsvData row, FilterCriteria filter) {
        String fieldValue = row.get(filter.getField());
        if (fieldValue == null) {
            return false;
        }
        
        Object filterValue = filter.getValue();
        String value = fieldValue.trim();
        
        return switch (filter.getOperator()) {
            case EQUALS -> compareEquals(value, filterValue, filter.isCaseSensitive());
            case NOT_EQUALS -> !compareEquals(value, filterValue, filter.isCaseSensitive());
            case CONTAINS -> compareContains(value, filterValue, filter.isCaseSensitive());
            case NOT_CONTAINS -> !compareContains(value, filterValue, filter.isCaseSensitive());
            case STARTS_WITH -> compareStartsWith(value, filterValue, filter.isCaseSensitive());
            case ENDS_WITH -> compareEndsWith(value, filterValue, filter.isCaseSensitive());
            case GREATER_THAN -> compareNumeric(value, filterValue) > 0;
            case LESS_THAN -> compareNumeric(value, filterValue) < 0;
            case GREATER_THAN_OR_EQUAL -> compareNumeric(value, filterValue) >= 0;
            case LESS_THAN_OR_EQUAL -> compareNumeric(value, filterValue) <= 0;
            case IN -> compareIn(value, filterValue, filter.isCaseSensitive());
            case NOT_IN -> !compareIn(value, filterValue, filter.isCaseSensitive());
        };
    }
    
    private boolean compareEquals(String value, Object filterValue, boolean caseSensitive) {
        String fv = filterValue.toString();
        return caseSensitive ? value.equals(fv) : value.equalsIgnoreCase(fv);
    }
    
    private boolean compareContains(String value, Object filterValue, boolean caseSensitive) {
        String fv = filterValue.toString();
        return caseSensitive ? value.contains(fv) : value.toLowerCase().contains(fv.toLowerCase());
    }
    
    private boolean compareStartsWith(String value, Object filterValue, boolean caseSensitive) {
        String fv = filterValue.toString();
        return caseSensitive ? value.startsWith(fv) : value.toLowerCase().startsWith(fv.toLowerCase());
    }
    
    private boolean compareEndsWith(String value, Object filterValue, boolean caseSensitive) {
        String fv = filterValue.toString();
        return caseSensitive ? value.endsWith(fv) : value.toLowerCase().endsWith(fv.toLowerCase());
    }
    
    private int compareNumeric(String value, Object filterValue) {
        try {
            double v = Double.parseDouble(value);
            double fv = Double.parseDouble(filterValue.toString());
            return Double.compare(v, fv);
        } catch (NumberFormatException e) {
            return value.compareTo(filterValue.toString());
        }
    }
    
    private boolean compareIn(String value, Object filterValue, boolean caseSensitive) {
        if (filterValue instanceof String) {
            String[] values = filterValue.toString().split(",");
            for (String v : values) {
                if (compareEquals(value, v.trim(), caseSensitive)) {
                    return true;
                }
            }
        } else if (filterValue instanceof List) {
            List<?> values = (List<?>) filterValue;
            for (Object v : values) {
                if (compareEquals(value, v, caseSensitive)) {
                    return true;
                }
            }
        }
        return false;
    }
}
