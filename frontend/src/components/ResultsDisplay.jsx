import React from 'react';
import './ResultsDisplay.css';

function ResultsDisplay({ data }) {
    if (!data || !data.data || data.data.length === 0) {
        return (
            <div className="results-container">
                <h2>Results</h2>
                <p className="no-data">No data to display</p>
            </div>
        );
    }

    return (
        <div className="results-container">
            <div className="results-header">
                <h2>Results</h2>
                <div className="results-info">
                    <span>Total Rows: <strong>{data.totalRows}</strong></span>
                    <span>Processing Time: <strong>{data.processingTimeMs}ms</strong></span>
                </div>
            </div>

            <div className="table-wrapper">
                <table className="results-table">
                    <thead>
                        <tr>
                            <th className="row-number">#</th>
                            {data.columns && data.columns.map(col => (
                                <th key={col}>{col}</th>
                            ))}
                        </tr>
                    </thead>
                    <tbody>
                        {data.data.map((row, index) => (
                            <tr key={index}>
                                <td className="row-number">{index + 1}</td>
                                {data.columns && data.columns.map(col => (
                                    <td key={`${index}-${col}`}>
                                        {formatCellValue(row[col])}
                                    </td>
                                ))}
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

function formatCellValue(value) {
    if (value === null || value === undefined) {
        return '-';
    }
    if (typeof value === 'number') {
        return value.toLocaleString(undefined, { maximumFractionDigits: 2 });
    }
    return value.toString();
}

export default ResultsDisplay;
