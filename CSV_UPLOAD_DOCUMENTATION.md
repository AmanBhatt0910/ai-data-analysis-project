# CSV Upload Functionality

This document describes the CSV upload feature implemented in the AI Data Analysis Dashboard.

## Overview

The CSV upload functionality allows users to upload CSV files, which are parsed and displayed in a table format on the dashboard. The system supports dynamic columns and robust error handling.

## Features

### Backend (Java/Spring Boot)

1. **REST API Endpoint**: `POST /api/csv/upload`
   - Accepts multipart form data with a CSV file
   - Returns parsed data with headers and rows

2. **CSV Parsing Service** (`CsvService`)
   - Uses Apache Commons CSV library
   - Parses CSV with automatic header detection
   - Handles UTF-8 encoding
   - Trims whitespace from values
   - Returns structured response with metadata

3. **Data Structures**
   - `CsvData`: Generic structure for dynamic columns (key-value pairs)
   - `CsvUploadResponse`: Response containing:
     - `success`: Boolean flag
     - `message`: Status message
     - `headers`: List of column names
     - `data`: List of CsvData objects
     - `rowCount`: Number of data rows
     - `errorDetails`: Error description if applicable

4. **Validation**
   - File type validation (must be .csv)
   - File size limits (10MB max)
   - CSV structure validation
   - Header presence check
   - Data rows validation

5. **Error Handling**
   - Invalid file type
   - Empty files
   - Missing headers
   - No data rows
   - Malformed CSV
   - Server-side errors

### Frontend (React)

1. **Upload UI Component** (`Upload.jsx`)
   - Drag-and-drop style file input
   - File type validation (CSV only)
   - File size validation
   - Loading state during upload

2. **Features**
   - Real-time file validation
   - Clear error messages
   - Success/failure handling
   - Data preview in table format
   - Row numbering
   - Responsive design

3. **Data Display**
   - Table showing all parsed data
   - Dynamic column rendering
   - Row count and column count display
   - Responsive scrolling for large datasets
   - Hover effects for better UX

4. **Styling** (`Upload.css`)
   - Professional, clean design
   - Responsive layout (mobile, tablet, desktop)
   - Accessibility features
   - Color-coded status messages
   - Smooth transitions and animations

## Configuration

### Backend Configuration (`application.properties`)
```properties
# File upload configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

### Frontend Configuration (`package.json`)
- Uses axios for HTTP requests
- Base URL: `http://localhost:8080/api`
- Environment variable: `VITE_API_BASE_URL`

## API Usage

### Request
```
POST /api/csv/upload
Content-Type: multipart/form-data

file: <CSV file>
```

### Success Response (200 OK)
```json
{
  "success": true,
  "message": "CSV file parsed successfully",
  "headers": ["id", "name", "email", "department", "salary", "date_joined"],
  "rowCount": 8,
  "data": [
    {
      "data": {
        "id": "1",
        "name": "John Doe",
        "email": "john.doe@example.com",
        "department": "Engineering",
        "salary": "95000",
        "date_joined": "2023-01-15"
      }
    },
    ...
  ]
}
```

### Error Response (400 Bad Request)
```json
{
  "success": false,
  "message": "Invalid file type. Only CSV files are allowed",
  "errorDetails": "File must have .csv extension"
}
```

## File Structure

```
backend/
├── src/main/java/com/ai/dashboard/backend/
│   ├── controller/
│   │   └── CsvController.java
│   ├── dto/
│   │   ├── CsvData.java
│   │   ├── CsvUploadRequest.java
│   │   └── CsvUploadResponse.java
│   └── service/
│       └── CsvService.java
└── src/main/resources/
    └── application.properties

frontend/
├── src/components/
│   ├── Upload.jsx
│   └── Upload.css
└── src/service/
    └── api.js
```

## Testing

### Sample CSV File
A sample CSV file (`sample_data.csv`) is included in the project root for testing purposes.

### Manual Testing Steps

1. **Start Backend**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

2. **Start Frontend**
   ```bash
   cd frontend
   npm run dev
   ```

3. **Test Upload**
   - Navigate to dashboard (after login/signup)
   - Click on "Upload CSV File" section
   - Select `sample_data.csv` or any other CSV file
   - Click Upload
   - View parsed data in the table

### Test Cases

1. **Valid CSV Upload**
   - Expected: File is parsed and displayed correctly

2. **Invalid File Type**
   - Expected: Error message "Please select a CSV file"

3. **File Too Large**
   - Expected: Error message "File size must be less than 10MB"

4. **Malformed CSV**
   - Expected: Appropriate error message with details

5. **Empty File**
   - Expected: Error message "No file provided"

## Dependencies

### Backend
- `org.apache.commons:commons-csv:1.10.0`
- Spring Boot 4.0.5
- Spring Data JPA
- Spring Security
- Lombok

### Frontend
- React 19.2.4
- axios 1.15.0
- Vite 8.0.4
- React Router DOM 7.3.0

## Security Considerations

1. **File Upload Security**
   - File type validation on both client and server
   - File size restrictions
   - Content-type checking

2. **CORS Configuration**
   - Allowed origins: `http://localhost:5173`, `http://localhost:3000`
   - Should be updated for production

3. **Authentication**
   - JWT token required for API access (interceptor in place)
   - User context available in controller

4. **Data Storage**
   - Currently stored in memory within response
   - Can be extended to persist to database

## Future Enhancements

1. **Data Persistence**
   - Store parsed data in database
   - Query and filter stored data

2. **Advanced Features**
   - Data validation against schema
   - Duplicate row detection
   - Data transformation/mapping
   - Export to different formats
   - Batch processing for large files

3. **UI Improvements**
   - Pagination for large datasets
   - Column sorting and filtering
   - Search functionality
   - Progress bar for large file uploads

4. **Analytics**
   - Data statistics and summaries
   - Visualization of data
   - Column data type detection

## Troubleshooting

### Backend Issues

1. **Port Already in Use**
   - Change `server.port` in application.properties
   - Or kill process on port 8080

2. **CORS Errors**
   - Ensure frontend is running on allowed origin
   - Check CORS configuration in CsvController

3. **File Upload Fails**
   - Check file size limit in application.properties
   - Verify file is valid CSV format

### Frontend Issues

1. **API Not Reachable**
   - Verify backend is running on http://localhost:8080
   - Check browser console for network errors
   - Verify VITE_API_BASE_URL environment variable

2. **File Input Not Working**
   - Clear browser cache
   - Check browser file API support
   - Verify file input permissions

3. **Table Not Displaying**
   - Check browser console for errors
   - Verify response structure matches expected format
   - Check CSS file is loaded

## Support

For issues or questions, refer to the main project documentation or contact the development team.
