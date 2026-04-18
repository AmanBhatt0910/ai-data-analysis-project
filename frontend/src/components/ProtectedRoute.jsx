import React from 'react';
import { useAuth } from '../context/AuthContext';

function ProtectedRoute({ children }) {
    const { isAuthenticated, loading } = useAuth();

    if (loading) {
        return <div>Loading...</div>;
    }

    if (!isAuthenticated) {
        window.location.href = '/login';
        return null;
    }

    return children;
}

export default ProtectedRoute;
