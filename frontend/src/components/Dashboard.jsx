import React from 'react';
import { useAuth } from '../context/AuthContext';
import Upload from './Upload';
import Chat from './Chat';
import './Dashboard.css';

function Dashboard() {
    const { user, logout } = useAuth();

    const handleLogout = () => {
        logout();
        window.location.href = '/login';
    };

    return (
        <div className="dashboard-container">
            <div className="dashboard-header">
                <h1>AI Data Analysis Dashboard</h1>
                <div className="user-section">
                    {user && (
                        <>
                            <span className="user-info">Welcome, {user.username}!</span>
                            <button onClick={handleLogout} className="logout-btn">
                                Logout
                            </button>
                        </>
                    )}
                </div>
            </div>
            <div className="dashboard-content">
                <Upload />
                <Chat />
            </div>
        </div>
    );
}

export default Dashboard;
