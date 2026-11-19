import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Wrench, CheckCircle, Clock, AlertTriangle, ArrowRight } from 'lucide-react';
import api from '../services/api';

export default function TechnicianDashboard() {
  const [incidents, setIncidents] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchAssigned = async () => {
    try {
      setLoading(true);
      const res = await api.get('/technicians/me/incidents');
      // Sort by priority (P1 -> P2 -> P3 -> P4)
      const priorityOrder = { P1: 1, P2: 2, P3: 3, P4: 4 };
      const sorted = res.data.sort((a, b) => priorityOrder[a.priority] - priorityOrder[b.priority]);
      setIncidents(sorted);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAssigned();
  }, []);

  const pendingCount = incidents.filter(i => i.status === 'ASSIGNED').length;
  const inProgressCount = incidents.filter(i => i.status === 'IN_PROGRESS' || i.status === 'ACKNOWLEDGED').length;
  const criticalCount = incidents.filter(i => i.priority === 'P1' && !['RESOLVED', 'CLOSED'].includes(i.status)).length;

  return (
    <div>
      <div style={{ marginBottom: '24px' }}>
        <h1 style={{ fontSize: '1.75rem', fontWeight: 800 }}>Technician Resolution Queue</h1>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginTop: '4px' }}>
          Prioritized active incident queue based on dynamic SLA targets
        </p>
      </div>

      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon" style={{ background: '#fef2f2', color: '#dc2626' }}>
            <AlertTriangle size={24} />
          </div>
          <div>
            <div className="stat-value">{criticalCount}</div>
            <div className="stat-label">Critical (P1) Active</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ background: '#fffbeb', color: '#d97706' }}>
            <Clock size={24} />
          </div>
          <div>
            <div className="stat-value">{pendingCount}</div>
            <div className="stat-label">Pending Acknowledgment</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ background: 'var(--primary-light)', color: 'var(--primary)' }}>
            <Wrench size={24} />
          </div>
          <div>
            <div className="stat-value">{inProgressCount}</div>
            <div className="stat-label">In Investigation</div>
          </div>
        </div>
      </div>

      <div className="table-wrapper">
        <table className="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Priority</th>
              <th>Incident Title</th>
              <th>Category</th>
              <th>Service</th>
              <th>Status</th>
              <th>SLA Resolution Target</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan="8" style={{ textAlign: 'center', padding: '32px', color: 'var(--text-muted)' }}>
                  Loading assigned tickets...
                </td>
              </tr>
            ) : incidents.length === 0 ? (
              <tr>
                <td colSpan="8" style={{ textAlign: 'center', padding: '32px', color: 'var(--text-muted)' }}>
                  No assigned incidents! Queue is all clear.
                </td>
              </tr>
            ) : (
              incidents.map((inc) => {
                const deadline = inc.slaResolutionDeadline ? new Date(inc.slaResolutionDeadline) : null;
                const isViolated = inc.slaViolated || (deadline && deadline < new Date() && !['RESOLVED', 'CLOSED'].includes(inc.status));

                return (
                  <tr key={inc.id} style={{ background: inc.priority === 'P1' && inc.status === 'ASSIGNED' ? '#fffaf0' : 'transparent' }}>
                    <td>
                      <Link to={`/incidents/${inc.id}`} style={{ fontWeight: 700, color: 'var(--primary)', textDecoration: 'none' }}>
                        {inc.incidentNumber}
                      </Link>
                    </td>
                    <td>
                      <span className={`badge badge-${inc.priority}`}>{inc.priority}</span>
                    </td>
                    <td>
                      <div style={{ fontWeight: 600 }}>{inc.title}</div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                        Reporter: {inc.reporterName}
                      </div>
                    </td>
                    <td>{inc.category}</td>
                    <td>{inc.serviceName}</td>
                    <td>
                      <span className={`badge badge-status badge-${inc.status}`}>{inc.status}</span>
                    </td>
                    <td>
                      {isViolated ? (
                        <span className="badge badge-sla-violated">BREACHED</span>
                      ) : (
                        <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                          {deadline ? deadline.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : 'N/A'}
                        </span>
                      )}
                    </td>
                    <td>
                      <Link to={`/incidents/${inc.id}`} className="btn btn-primary btn-sm">
                        Work Ticket <ArrowRight size={13} />
                      </Link>
                    </td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
