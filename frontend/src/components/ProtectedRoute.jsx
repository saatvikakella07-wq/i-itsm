import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function ProtectedRoute({ children, allowedRoles }) {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '60vh' }}>
        <div style={{ fontSize: '1rem', color: 'var(--text-muted)' }}>Loading session...</div>
      </div>
    );
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && !allowedRoles.includes(user.role)) {
    // Redirect to default home page of that user's role
    if (user.role === 'ADMIN') return <Navigate to="/admin" replace />;
    if (user.role === 'TECHNICIAN') return <Navigate to="/technician" replace />;
    return <Navigate to="/dashboard" replace />;
  }

  return children;
}
