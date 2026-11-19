import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { PlusCircle, Search, Clock, CheckCircle, AlertTriangle, ArrowRight } from 'lucide-react';
import api from '../services/api';

export default function EmployeeDashboard() {
  const [incidents, setIncidents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('');

  const fetchIncidents = async () => {
    try {
      setLoading(true);
      const params = {};
      if (statusFilter) params.status = statusFilter;
      if (searchTerm) params.q = searchTerm;
      const res = await api.get('/incidents', { params });
      setIncidents(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchIncidents();
  }, [statusFilter]);

  const handleSearch = (e) => {
    e.preventDefault();
    fetchIncidents();
  };

  const activeCount = incidents.filter(i => !['RESOLVED', 'CLOSED'].includes(i.status)).length;
  const resolvedCount = incidents.filter(i => ['RESOLVED', 'CLOSED'].includes(i.status)).length;

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <div>
          <h1 style={{ fontSize: '1.75rem', fontWeight: 800 }}>Employee Portal</h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginTop: '4px' }}>
            Track and report service interruptions or technical issues
          </p>
        </div>
        <Link to="/incidents/new" className="btn btn-primary">
          <PlusCircle size={18} />
          Report New Incident
        </Link>
      </div>

      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon" style={{ background: 'var(--primary-light)', color: 'var(--primary)' }}>
            <Clock size={24} />
          </div>
          <div>
            <div className="stat-value">{incidents.length}</div>
            <div className="stat-label">Total Reported</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ background: '#fffbeb', color: '#d97706' }}>
            <AlertTriangle size={24} />
          </div>
          <div>
            <div className="stat-value">{activeCount}</div>
            <div className="stat-label">In Progress / Active</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ background: '#ecfdf5', color: '#059669' }}>
            <CheckCircle size={24} />
          </div>
          <div>
            <div className="stat-value">{resolvedCount}</div>
            <div className="stat-label">Resolved & Closed</div>
          </div>
        </div>
      </div>

      <div className="card" style={{ marginBottom: '24px' }}>
        <form onSubmit={handleSearch} style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
          <div style={{ flex: 1, minWidth: '240px', position: 'relative' }}>
            <input
              type="text"
              className="form-control"
              placeholder="Search by title, description or INC number..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
          <div style={{ width: '180px' }}>
            <select
              className="form-control"
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
            >
              <option value="">All Statuses</option>
              <option value="NEW">New</option>
              <option value="ASSIGNED">Assigned</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="RESOLVED">Resolved</option>
              <option value="CLOSED">Closed</option>
            </select>
          </div>
          <button type="submit" className="btn btn-secondary">
            <Search size={16} />
            Filter
          </button>
        </form>
      </div>

      <div className="table-wrapper">
        <table className="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Title</th>
              <th>Service</th>
              <th>Priority</th>
              <th>Status</th>
              <th>Technician</th>
              <th>Reported</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan="8" style={{ textAlign: 'center', padding: '32px', color: 'var(--text-muted)' }}>
                  Loading incidents...
                </td>
              </tr>
            ) : incidents.length === 0 ? (
              <tr>
                <td colSpan="8" style={{ textAlign: 'center', padding: '32px', color: 'var(--text-muted)' }}>
                  No incidents found. Click "Report New Incident" to file a ticket.
                </td>
              </tr>
            ) : (
              incidents.map((inc) => (
                <tr key={inc.id}>
                  <td>
                    <Link to={`/incidents/${inc.id}`} style={{ fontWeight: 600, color: 'var(--primary)', textDecoration: 'none' }}>
                      {inc.incidentNumber}
                    </Link>
                  </td>
                  <td>
                    <div style={{ fontWeight: 600 }}>{inc.title}</div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{inc.category}</div>
                  </td>
                  <td>{inc.serviceName}</td>
                  <td>
                    <span className={`badge badge-${inc.priority}`}>{inc.priority}</span>
                  </td>
                  <td>
                    <span className={`badge badge-status badge-${inc.status}`}>{inc.status}</span>
                  </td>
                  <td>{inc.technicianName || <span style={{ color: 'var(--text-light)' }}>Unassigned</span>}</td>
                  <td style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                    {new Date(inc.createdAt).toLocaleDateString([], { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })}
                  </td>
                  <td>
                    <Link to={`/incidents/${inc.id}`} className="btn btn-secondary btn-sm">
                      Details <ArrowRight size={13} />
                    </Link>
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
