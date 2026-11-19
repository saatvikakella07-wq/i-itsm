import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Search, Filter, RefreshCw, UserCheck, ArrowRight } from 'lucide-react';
import api from '../services/api';

const CATEGORIES = [
  'HARDWARE', 'SOFTWARE', 'NETWORK', 'DATABASE',
  'CLOUD', 'SECURITY', 'AUTH', 'APPLICATION', 'STORAGE'
];

export default function AdminIncidentsPage() {
  const [incidents, setIncidents] = useState([]);
  const [technicians, setTechnicians] = useState([]);
  const [loading, setLoading] = useState(true);

  // Filters (FR-016: 5 criteria minimum)
  const [status, setStatus] = useState('');
  const [priority, setPriority] = useState('');
  const [category, setCategory] = useState('');
  const [search, setSearch] = useState('');

  // Reassign Modal State
  const [selectedIncident, setSelectedIncident] = useState(null);
  const [selectedTechId, setSelectedTechId] = useState('');
  const [reassignLoading, setReassignLoading] = useState(false);

  const fetchIncidents = async () => {
    try {
      setLoading(true);
      const params = {};
      if (status) params.status = status;
      if (priority) params.priority = priority;
      if (category) params.category = category;
      if (search) params.q = search;

      const res = await api.get('/incidents', { params });
      setIncidents(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const fetchTechnicians = async () => {
    try {
      const res = await api.get('/technicians');
      setTechnicians(res.data);
      if (res.data.length > 0) setSelectedTechId(res.data[0].userId);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchIncidents();
    fetchTechnicians();
  }, [status, priority, category]);

  const handleSearch = (e) => {
    e.preventDefault();
    fetchIncidents();
  };

  const handleReassign = async (e) => {
    e.preventDefault();
    if (!selectedIncident || !selectedTechId) return;

    try {
      setReassignLoading(true);
      await api.patch(`/incidents/${selectedIncident.id}/assign`, {
        technicianId: Number(selectedTechId),
      });
      setSelectedIncident(null);
      await fetchIncidents();
    } catch (err) {
      alert(err.response?.data?.error || 'Reassignment failed');
    } finally {
      setReassignLoading(false);
    }
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <div>
          <h1 style={{ fontSize: '1.75rem', fontWeight: 800 }}>Incident Management Center</h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginTop: '4px' }}>
            Filter, inspect, and reassign all enterprise support incidents
          </p>
        </div>
      </div>

      {/* 5+ Criteria Filter Bar (FR-016) */}
      <div className="card" style={{ marginBottom: '20px' }}>
        <form onSubmit={handleSearch} style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr)) 100px', gap: '12px' }}>
          <div>
            <input
              type="text"
              className="form-control"
              placeholder="Search keyword..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>

          <div>
            <select className="form-control" value={status} onChange={(e) => setStatus(e.target.value)}>
              <option value="">All Statuses</option>
              <option value="NEW">New</option>
              <option value="CLASSIFIED">Classified</option>
              <option value="ASSIGNED">Assigned</option>
              <option value="ACKNOWLEDGED">Acknowledged</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="RESOLVED">Resolved</option>
              <option value="CLOSED">Closed</option>
            </select>
          </div>

          <div>
            <select className="form-control" value={priority} onChange={(e) => setPriority(e.target.value)}>
              <option value="">All Priorities</option>
              <option value="P1">P1 - Critical</option>
              <option value="P2">P2 - High</option>
              <option value="P3">P3 - Medium</option>
              <option value="P4">P4 - Low</option>
            </select>
          </div>

          <div>
            <select className="form-control" value={category} onChange={(e) => setCategory(e.target.value)}>
              <option value="">All Categories</option>
              {CATEGORIES.map(c => <option key={c} value={c}>{c}</option>)}
            </select>
          </div>

          <button type="submit" className="btn btn-primary" style={{ height: '38px' }}>
            <Search size={16} /> Filter
          </button>
        </form>
      </div>

      <div className="table-wrapper">
        <table className="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Priority</th>
              <th>Incident Title</th>
              <th>Category</th>
              <th>Reporter</th>
              <th>Assigned Technician</th>
              <th>Status</th>
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
                  No incidents match the selected filter criteria.
                </td>
              </tr>
            ) : (
              incidents.map((inc) => (
                <tr key={inc.id}>
                  <td>
                    <Link to={`/incidents/${inc.id}`} style={{ fontWeight: 700, color: 'var(--primary)', textDecoration: 'none' }}>
                      {inc.incidentNumber}
                    </Link>
                  </td>
                  <td><span className={`badge badge-${inc.priority}`}>{inc.priority}</span></td>
                  <td>
                    <div style={{ fontWeight: 600 }}>{inc.title}</div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{inc.serviceName}</div>
                  </td>
                  <td>{inc.category}</td>
                  <td>{inc.reporterName}</td>
                  <td>
                    <span style={{ fontWeight: 500 }}>
                      {inc.technicianName || <span style={{ color: 'var(--text-light)' }}>Unassigned</span>}
                    </span>
                  </td>
                  <td><span className={`badge badge-status badge-${inc.status}`}>{inc.status}</span></td>
                  <td>
                    <div style={{ display: 'flex', gap: '6px' }}>
                      <button
                        onClick={() => setSelectedIncident(inc)}
                        className="btn btn-secondary btn-sm"
                        title="Reassign technician"
                      >
                        <UserCheck size={14} /> Reassign
                      </button>
                      <Link to={`/incidents/${inc.id}`} className="btn btn-secondary btn-sm">
                        <ArrowRight size={14} />
                      </Link>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Reassign Modal (Task T040) */}
      {selectedIncident && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3 style={{ marginBottom: '8px' }}>Reassign Technician</h3>
            <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '16px' }}>
              Reassigning ticket <strong>{selectedIncident.incidentNumber}</strong> ({selectedIncident.title})
            </p>
            <form onSubmit={handleReassign}>
              <div className="form-group">
                <label className="form-label">Select Technician</label>
                <select
                  className="form-control"
                  value={selectedTechId}
                  onChange={(e) => setSelectedTechId(e.target.value)}
                  required
                >
                  {technicians.map((t) => (
                    <option key={t.technicianId} value={t.userId}>
                      {t.name} (Load: {t.currentWorkload} tickets)
                    </option>
                  ))}
                </select>
              </div>
              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '20px' }}>
                <button
                  type="button"
                  onClick={() => setSelectedIncident(null)}
                  className="btn btn-secondary"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="btn btn-primary"
                  disabled={reassignLoading}
                >
                  Confirm Reassignment
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
