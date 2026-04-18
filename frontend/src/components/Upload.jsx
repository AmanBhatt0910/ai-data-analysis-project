import React, { useState } from "react";
import API from "../service/api";
import "./Upload.css";

function Upload() {
    const [file, setFile] = useState(null);
    const [loading, setLoading] = useState(false);
    const [uploadData, setUploadData] = useState(null);
    const [error, setError] = useState(null);

    const handleFileChange = (e) => {
        const selectedFile = e.target.files[0];
        
        // Validate file type
        if (selectedFile) {
            if (!selectedFile.name.toLowerCase().endsWith('.csv')) {
                setError("Please select a CSV file");
                setFile(null);
                return;
            }
            
            // Validate file size (10MB)
            if (selectedFile.size > 10 * 1024 * 1024) {
                setError("File size must be less than 10MB");
                setFile(null);
                return;
            }
            
            setError(null);
            setFile(selectedFile);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!file) {
            setError("Please select a CSV file");
            return;
        }

        setLoading(true);
        setError(null);

        try {
            const formData = new FormData();
            formData.append("file", file);

            const response = await API.post("/csv/upload", formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                },
            });

            if (response.data.success) {
                setUploadData(response.data);
                setError(null);
                setFile(null);
                // Clear file input
                document.getElementById("csvFile").value = "";
            } else {
                setError(response.data.errorDetails || response.data.message);
                setUploadData(null);
            }
        } catch (err) {
            const errorMessage = err.response?.data?.errorDetails || 
                               err.response?.data?.message || 
                               err.message || 
                               "Failed to upload file";
            setError(errorMessage);
            setUploadData(null);
        } finally {
            setLoading(false);
        }
    };

    const handleClearData = () => {
        setUploadData(null);
        setFile(null);
        setError(null);
    };

    return (
        <div className="upload-container">
            <div className="upload-section">
                <h2>Upload CSV File</h2>
                <form onSubmit={handleSubmit} className="upload-form">
                    <div className="file-input-wrapper">
                        <input
                            id="csvFile"
                            type="file"
                            accept=".csv"
                            onChange={handleFileChange}
                            className="file-input"
                        />
                        <label htmlFor="csvFile" className="file-label">
                            {file ? file.name : "Choose CSV file..."}
                        </label>
                    </div>

                    {error && <div className="error-message">{error}</div>}

                    <div className="upload-actions">
                        <button
                            type="submit"
                            disabled={!file || loading}
                            className="upload-btn"
                        >
                            {loading ? "Uploading..." : "Upload"}
                        </button>
                        {uploadData && (
                            <button
                                type="button"
                                onClick={handleClearData}
                                className="clear-btn"
                            >
                                Clear
                            </button>
                        )}
                    </div>
                </form>
            </div>

            {uploadData && (
                <div className="data-display-section">
                    <h2>Uploaded Data Preview</h2>
                    <div className="data-info">
                        <p className="info-text">
                            Total Rows: <strong>{uploadData.rowCount}</strong>
                        </p>
                        <p className="info-text">
                            Columns: <strong>{uploadData.headers.length}</strong>
                        </p>
                    </div>

                    <div className="table-wrapper">
                        <table className="data-table">
                            <thead>
                                <tr>
                                    <th className="row-number">#</th>
                                    {uploadData.headers.map((header) => (
                                        <th key={header}>{header}</th>
                                    ))}
                                </tr>
                            </thead>
                            <tbody>
                                {uploadData.data.map((row, index) => (
                                    <tr key={index}>
                                        <td className="row-number">{index + 1}</td>
                                        {uploadData.headers.map((header) => (
                                            <td key={`${index}-${header}`}>
                                                {row.data[header] || "-"}
                                            </td>
                                        ))}
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}
        </div>
    );
}

export default Upload;