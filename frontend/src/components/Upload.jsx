import React, {useState} from "react";
import API from "../service/api";

function Upload() {
    const [file, setFile] = useState(null);

    const handleSubmit = async () => {
        const formData = new FormData();
        formData.append("file", file);

        await API.post("/upload", formData);
        alert("File uploaded successfully!");
    }
    return (
        <div>
            <h1>Upload File</h1>
            <input type="file" onChange={(e) => setFile(e.target.files[0])} />
            <button onClick={handleSubmit}>Upload</button>
        </div>
    )
}

export default Upload;