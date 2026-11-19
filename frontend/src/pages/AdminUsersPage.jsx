import React, { useState, useEffect } from 'react';
import { Users, CheckCircle, XCircle } from 'lucide-react';
import api from '../services/api';

export default function AdminUsersPage() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const res = await api.get('/users');
      setUsers(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleRoleChange = async (userId, newRole) => {
    try {
      await api.put(`/users/${userId}`, { role: newRole });
      setUsers(prev => prev.map(u => u.id === userId ? { ...u, role: newRole } : u));
    } catch (err) {
      alert(err.response?.data?.error || 'Failed to update role');
    }
  };

  const handleToggleActive = async (userId, currentActive) => {
    try {
      await api.put(`/users/${userId}`, { isActive: !currentActive });
      setUsers(prev => prev.map(u => u.id === userId ? { ...u, isActive: !currentActive } : u));
    } catch (err) {
      alert(err.response?.data?.error || 'Failed to toggle status');
    }
  };

  return (
    <div>
      <div style={{ marginBottom: '24px' }}>
        <h1 style={{ fontSize: '1.75rem', fontWeight: 800 }}>User Account Management</h1>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginTop: '4px' }}>
          Manage user permissions, employee roles, and account active states
        </p>
      </div>

      <div className="table-wrapper">
        <table className="table">
          <thead>
            <tr>
              <th>User ID</th>
              <th>Full Name</th>
              <th>Email</th>
              <th>Assigned Role</th>
              <th>Status</th>
              <th>Joined Date</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan="7" style={{ textAlign: 'center', padding: '32px', color: 'var(--text-muted)' }}>
                  Loading user accounts...
                </td>
              </tr>
            ) : (
              users.map((u) => (
                <tr key={u.id}>
                  <td>#{u.id}</td>
                  <td style={{ fontWeight: 600 }}>{u.name}</td>
                  <td>{u.email}</td>
                  <td>
                    <select
                      className="form-control"
                      style={{ padding: '4px 8px', fontSize: '0.82rem', width: 'auto' }}
                      value={u.role}
                      onChange={(e) => handleRoleChange(u.id, e.target.value)}
                    >
                      <option value="EMPLOYEE">EMPLOYEE</option>
                      <option value="TECHNICIAN">TECHNICIAN</option>
                      <option value="ADMIN">ADMIN</option>
                    </select>
                  </td>
                  <td>
                    {u.isActive ? (
                      <span className="badge" style={{ background: '#ecfdf5', color: '#059669' }}>Active</span>
                    ) : (
                      <span className="badge" style={{ background: '#fef2f2', color: '#dc2626' }}>Disabled</span>
                    )}
                  </td>
                  <td style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                    {new Date(u.createdAt).toLocaleDateString()}
                  </td>
                  <td>
                    <button
                      onClick={() => handleToggleActive(u.id, u.isActive)}
                      className={`btn btn-sm ${u.isActive ? 'btn-secondary' : 'btn-success'}`}
                      style={{ fontSize: '0.75rem' }}
                    >
                      {u.isActive ? 'Deactivate' : 'Activate'}
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
