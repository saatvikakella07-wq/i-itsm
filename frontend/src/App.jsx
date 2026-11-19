import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Navbar from './components/Navbar';
import ProtectedRoute from './components/ProtectedRoute';

import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import EmployeeDashboard from './pages/EmployeeDashboard';
import CreateIncidentPage from './pages/CreateIncidentPage';
import IncidentDetailPage from './pages/IncidentDetailPage';
import TechnicianDashboard from './pages/TechnicianDashboard';
import AdminDashboard from './pages/AdminDashboard';
import AdminIncidentsPage from './pages/AdminIncidentsPage';
import AdminUsersPage from './pages/AdminUsersPage';

function RootRedirect() {
  const { user, loading } = useAuth();
  if (loading) return null;
  if (!user) return <Navigate to="/login" replace />;
  if (user.role === 'ADMIN') return <Navigate to="/admin" replace />;
  if (user.role === 'TECHNICIAN') return <Navigate to="/technician" replace />;
  return <Navigate to="/dashboard" replace />;
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <div className="app-layout">
          <Navbar />
          <main className="main-content">
            <Routes>
              {/* Public Routes */}
              <Route path="/login" element={<LoginPage />} />
              <Route path="/register" element={<RegisterPage />} />

              {/* Employee Routes */}
              <Route
                path="/dashboard"
                element={
                  <ProtectedRoute allowedRoles={['EMPLOYEE', 'ADMIN']}>
                    <EmployeeDashboard />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/incidents/new"
                element={
                  <ProtectedRoute allowedRoles={['EMPLOYEE', 'ADMIN']}>
                    <CreateIncidentPage />
                  </ProtectedRoute>
                }
              />

              {/* Incident Details (Accessible by all authenticated roles) */}
              <Route
                path="/incidents/:id"
                element={
                  <ProtectedRoute>
                    <IncidentDetailPage />
                  </ProtectedRoute>
                }
              />

              {/* Technician Queue */}
              <Route
                path="/technician"
                element={
                  <ProtectedRoute allowedRoles={['TECHNICIAN', 'ADMIN']}>
                    <TechnicianDashboard />
                  </ProtectedRoute>
                }
              />

              {/* Administrator Portal */}
              <Route
                path="/admin"
                element={
                  <ProtectedRoute allowedRoles={['ADMIN']}>
                    <AdminDashboard />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/incidents"
                element={
                  <ProtectedRoute allowedRoles={['ADMIN']}>
                    <AdminIncidentsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/users"
                element={
                  <ProtectedRoute allowedRoles={['ADMIN']}>
                    <AdminUsersPage />
                  </ProtectedRoute>
                }
              />

              {/* Default Redirect */}
              <Route path="/" element={<RootRedirect />} />
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </main>
        </div>
      </BrowserRouter>
    </AuthProvider>
  );
}
