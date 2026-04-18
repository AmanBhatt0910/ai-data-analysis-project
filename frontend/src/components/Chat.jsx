import React, {useState} from "react";
import API from "../service/api";

function Chat() {
    const [query, setQuery] = useState("");

    const sendQuery = async() => {
        const response = await API.post("/query", {query});
        console.log(response.data);
    }
    return (
        <div>
            <input type="text" onChange={(e) => setQuery(e.target.value)} placeholder="Type you message here..."/>
            <button onClick={sendQuery}>Ask</button>
        </div>
    )
}

export default Chat;