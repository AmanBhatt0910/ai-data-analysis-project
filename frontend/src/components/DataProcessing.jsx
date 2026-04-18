import React, { useState, useEffect } from 'react';
import API from '../service/api';
import ResultsDisplay from './ResultsDisplay';
import ChartDisplay from './ChartDisplay';
import './DataProcessing.css';

function DataProcessing({ sessionId, columns, rowCount }) {
    const [filters, setFilters] = useState([]);
    const [groupBy, setGroupBy] = useState([]);
    const [aggregations, setAggregations] = useState([]);
    const [sortBy, setSortBy] = useState('');
    const [sortOrder, setSortOrder] = useState('ASC');
    const [limit, setLimit] = useState(100);
    const [results, setResults] = useState(null);
    const [chartData, setChartData] = useState(null);
    const [chartType, setChartType] = useState('bar');
    const [xAxisField, setXAxisField] = useState('');
    const [yAxisField, setYAxisField] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [showChart, setShowChart] = useState(false);

    useEffect(() => {
        if (columns && columns.length > 0) {
            setXAxisField(columns[0]);
            setYAxisField(columns[columns.length > 1 ? 1 : 0]);
        }
    }, [columns]);

    const addFilter = () => {
        setFilters([...filters, {
            field: columns ? columns[0] : '',
            operator: 'EQUALS',
            value: '',
            caseSensitive: false
        }]);
    };

    const updateFilter = (index, updatedFilter) => {
        const newFilters = [...filters];
        newFilters[index] = updatedFilter;
        setFilters(newFilters);
    };

    const removeFilter = (index) => {
        setFilters(filters.filter((_, i) => i !== index));
    };

    const toggleGroupBy = (field) => {
        if (groupBy.includes(field)) {
            setGroupBy(groupBy.filter(f => f !== field));
        } else {
            setGroupBy([...groupBy, field]);
        }
    };

    const addAggregation = () => {
        setAggregations([...aggregations, {
            field: columns ? columns[0] : '',
            type: 'SUM',
            alias: ''
        }]);
    };

    const updateAggregation = (index, updatedAgg) => {
        const newAggs = [...aggregations];
        newAggs[index] = updatedAgg;
        setAggregations(newAggs);
    };

    const removeAggregation = (index) => {
        setAggregations(aggregations.filter((_, i) => i !== index));
    };

    const handleProcessData = async () => {
        setLoading(true);
        setError(null);

        try {
            const request = {
                filters: filters.length > 0 ? filters : null,
                groupByFields: groupBy.length > 0 ? groupBy : null,
                aggregations: aggregations.length > 0 ? aggregations : null,
                sortBy: sortBy || null,
                sortOrder: sortOrder,
                limit: limit,
                offset: 0
            };

            const response = await API.post(`/data-processing/process?sessionId=${sessionId}`, request);

            if (response.data.success) {
                setResults(response.data);
                setChartData(null);
                setShowChart(false);
            } else {
                setError(response.data.message);
            }
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to process data');
        } finally {
            setLoading(false);
        }
    };

    const handleGenerateChart = async () => {
        if (!xAxisField || !yAxisField) {
            setError('Please select both X and Y axis fields');
            return;
        }

        setLoading(true);
        setError(null);

        try {
            const request = results ? {
                filters: filters.length > 0 ? filters : null,
                groupByFields: groupBy.length > 0 ? groupBy : null,
                aggregations: aggregations.length > 0 ? aggregations : null,
                sortBy: sortBy || null,
                sortOrder: sortOrder,
                limit: limit,
                offset: 0
            } : null;

            const url = new URL(`${API.defaults.baseURL}/data-processing/chart-data`, window.location.origin);
            url.searchParams.append('sessionId', sessionId);
            url.searchParams.append('chartType', chartType);
            url.searchParams.append('xAxisField', xAxisField);
            url.searchParams.append('yAxisField', yAxisField);

            const response = await API.post(url.pathname + url.search, request);

            if (response.data.success) {
                setChartData(response.data);
                setShowChart(true);
            } else {
                setError(response.data.message);
            }
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to generate chart');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="data-processing-container">
            <div className="processing-panel">
                <h2>Data Processing</h2>

                {/* Info Section */}
                <div className="info-section">
                    <p>Total Rows Available: <strong>{rowCount}</strong></p>
                </div>

                {/* Filters Section */}
                <div className="section filters-section">
                    <h3>Filters</h3>
                    <div className="filter-list">
                        {filters.map((filter, index) => (
                            <div key={index} className="filter-row">
                                <select
                                    value={filter.field}
                                    onChange={(e) => updateFilter(index, { ...filter, field: e.target.value })}
                                >
                                    {columns?.map(col => <option key={col} value={col}>{col}</option>)}
                                </select>

                                <select
                                    value={filter.operator}
                                    onChange={(e) => updateFilter(index, { ...filter, operator: e.target.value })}
                                >
                                    <option value="EQUALS">Equals</option>
                                    <option value="NOT_EQUALS">Not Equals</option>
                                    <option value="CONTAINS">Contains</option>
                                    <option value="NOT_CONTAINS">Not Contains</option>
                                    <option value="STARTS_WITH">Starts With</option>
                                    <option value="ENDS_WITH">Ends With</option>
                                    <option value="GREATER_THAN">Greater Than</option>
                                    <option value="LESS_THAN">Less Than</option>
                                    <option value="GREATER_THAN_OR_EQUAL">≥</option>
                                    <option value="LESS_THAN_OR_EQUAL">≤</option>
                                </select>

                                <input
                                    type="text"
                                    placeholder="Value"
                                    value={filter.value}
                                    onChange={(e) => updateFilter(index, { ...filter, value: e.target.value })}
                                />

                                <label className="checkbox-label">
                                    <input
                                        type="checkbox"
                                        checked={filter.caseSensitive}
                                        onChange={(e) => updateFilter(index, { ...filter, caseSensitive: e.target.checked })}
                                    />
                                    Case Sensitive
                                </label>

                                <button onClick={() => removeFilter(index)} className="remove-btn">×</button>
                            </div>
                        ))}
                    </div>
                    <button onClick={addFilter} className="add-btn">+ Add Filter</button>
                </div>

                {/* Group By Section */}
                <div className="section group-section">
                    <h3>Group By</h3>
                    <div className="checkbox-group">
                        {columns?.map(col => (
                            <label key={col} className="checkbox-label">
                                <input
                                    type="checkbox"
                                    checked={groupBy.includes(col)}
                                    onChange={() => toggleGroupBy(col)}
                                />
                                {col}
                            </label>
                        ))}
                    </div>
                </div>

                {/* Aggregation Section */}
                <div className="section aggregation-section">
                    <h3>Aggregations</h3>
                    <div className="aggregation-list">
                        {aggregations.map((agg, index) => (
                            <div key={index} className="aggregation-row">
                                <select
                                    value={agg.type}
                                    onChange={(e) => updateAggregation(index, { ...agg, type: e.target.value })}
                                >
                                    <option value="SUM">Sum</option>
                                    <option value="AVG">Average</option>
                                    <option value="COUNT">Count</option>
                                    <option value="MIN">Min</option>
                                    <option value="MAX">Max</option>
                                    <option value="DISTINCT_COUNT">Distinct Count</option>
                                </select>

                                <select
                                    value={agg.field}
                                    onChange={(e) => updateAggregation(index, { ...agg, field: e.target.value })}
                                >
                                    {columns?.map(col => <option key={col} value={col}>{col}</option>)}
                                </select>

                                <input
                                    type="text"
                                    placeholder="Custom alias (optional)"
                                    value={agg.alias}
                                    onChange={(e) => updateAggregation(index, { ...agg, alias: e.target.value })}
                                />

                                <button onClick={() => removeAggregation(index)} className="remove-btn">×</button>
                            </div>
                        ))}
                    </div>
                    <button onClick={addAggregation} className="add-btn">+ Add Aggregation</button>
                </div>

                {/* Sorting & Limits Section */}
                <div className="section sort-section">
                    <h3>Sorting & Limits</h3>
                    <div className="sort-controls">
                        <select value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
                            <option value="">-- No Sorting --</option>
                            {columns?.map(col => <option key={col} value={col}>{col}</option>)}
                        </select>

                        <select value={sortOrder} onChange={(e) => setSortOrder(e.target.value)}>
                            <option value="ASC">Ascending</option>
                            <option value="DESC">Descending</option>
                        </select>

                        <input
                            type="number"
                            min="1"
                            max="1000"
                            value={limit}
                            onChange={(e) => setLimit(parseInt(e.target.value))}
                            placeholder="Limit"
                        />
                    </div>
                </div>

                {/* Chart Configuration */}
                <div className="section chart-config">
                    <h3>Chart Configuration</h3>
                    <div className="chart-controls">
                        <select value={chartType} onChange={(e) => setChartType(e.target.value)}>
                            <option value="bar">Bar Chart</option>
                            <option value="line">Line Chart</option>
                            <option value="pie">Pie Chart</option>
                        </select>

                        <select value={xAxisField} onChange={(e) => setXAxisField(e.target.value)}>
                            <option value="">Select X-Axis Field</option>
                            {columns?.map(col => <option key={col} value={col}>{col}</option>)}
                        </select>

                        <select value={yAxisField} onChange={(e) => setYAxisField(e.target.value)}>
                            <option value="">Select Y-Axis Field</option>
                            {columns?.map(col => <option key={col} value={col}>{col}</option>)}
                        </select>
                    </div>
                </div>

                {/* Error Display */}
                {error && <div className="error-message">{error}</div>}

                {/* Action Buttons */}
                <div className="action-buttons">
                    <button
                        onClick={handleProcessData}
                        disabled={loading}
                        className="primary-btn"
                    >
                        {loading ? 'Processing...' : 'Process Data'}
                    </button>
                    <button
                        onClick={handleGenerateChart}
                        disabled={loading || !results}
                        className="secondary-btn"
                    >
                        {loading ? 'Generating...' : 'Generate Chart'}
                    </button>
                </div>
            </div>

            {/* Results Display */}
            {results && <ResultsDisplay data={results} />}

            {/* Chart Display */}
            {showChart && chartData && <ChartDisplay data={chartData} />}
        </div>
    );
}

export default DataProcessing;
