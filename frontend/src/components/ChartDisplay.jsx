import React from 'react';
import {
    BarChart, Bar,
    LineChart, Line,
    PieChart, Pie,
    XAxis, YAxis, CartesianGrid, Tooltip, Legend, Cell,
    ResponsiveContainer
} from 'recharts';
import './ChartDisplay.css';

function ChartDisplay({ data }) {
    if (!data || !data.chartData || data.chartData.length === 0) {
        return (
            <div className="chart-container">
                <h2>Chart</h2>
                <p className="no-data">No data to display chart</p>
            </div>
        );
    }

    const chartConfig = data.chartConfig;
    const chartType = data.chartType;
    const chartData = data.chartData;

    const COLORS = [
        '#8884d8', '#82ca9d', '#ffc658', '#ff7c7c', '#8dd1e1',
        '#d084d0', '#ffa07a', '#98d8c8', '#f7dc6f', '#bb8fce'
    ];

    return (
        <div className="chart-container">
            <div className="chart-header">
                <h2>{chartConfig?.title || 'Chart'}</h2>
            </div>

            <div className="chart-wrapper">
                <ResponsiveContainer width="100%" height={400}>
                    {chartType === 'bar' && (
                        <BarChart data={chartData} margin={{ top: 20, right: 30, left: 20, bottom: 20 }}>
                            <CartesianGrid strokeDasharray="3 3" />
                            <XAxis
                                dataKey={Object.keys(chartData[0])?.[0]}
                                label={{ value: chartConfig?.xAxisLabel, position: 'insideBottomRight', offset: -10 }}
                            />
                            <YAxis label={{ value: chartConfig?.yAxisLabel, angle: -90, position: 'insideLeft' }} />
                            <Tooltip />
                            <Legend />
                            <Bar dataKey={Object.keys(chartData[0])?.[1]} fill="#8884d8" />
                        </BarChart>
                    )}

                    {chartType === 'line' && (
                        <LineChart data={chartData} margin={{ top: 20, right: 30, left: 20, bottom: 20 }}>
                            <CartesianGrid strokeDasharray="3 3" />
                            <XAxis
                                dataKey={Object.keys(chartData[0])?.[0]}
                                label={{ value: chartConfig?.xAxisLabel, position: 'insideBottomRight', offset: -10 }}
                            />
                            <YAxis label={{ value: chartConfig?.yAxisLabel, angle: -90, position: 'insideLeft' }} />
                            <Tooltip />
                            <Legend />
                            <Line type="monotone" dataKey={Object.keys(chartData[0])?.[1]} stroke="#8884d8" />
                        </LineChart>
                    )}

                    {chartType === 'pie' && (
                        <PieChart margin={{ top: 20, right: 30, left: 20, bottom: 20 }}>
                            <Pie
                                data={chartData}
                                cx="50%"
                                cy="50%"
                                labelLine={true}
                                label={renderLabel}
                                outerRadius={100}
                                fill="#8884d8"
                                dataKey={Object.keys(chartData[0])?.[1]}
                            >
                                {chartData.map((entry, index) => (
                                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                                ))}
                            </Pie>
                            <Tooltip />
                            <Legend />
                        </PieChart>
                    )}
                </ResponsiveContainer>
            </div>
        </div>
    );
}

function renderLabel(entry) {
    return entry[Object.keys(entry)[0]];
}

export default ChartDisplay;
