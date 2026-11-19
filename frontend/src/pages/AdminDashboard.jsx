import React, { useState, useEffect } from 'react';
import {
  Layers, AlertOctagon, CheckCircle2, Clock, AlertTriangle, Users,
  Server, BarChart3
} from 'lucide-react';
import api from '../services/api';

export default function AdminDashboard() {
  const [metrics, setMetrics] = useState(null);
  const [technicians, setTechnicians] = useState([]);
  const [serviceAnalysis, setServiceAnalysis] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [dashRes, techRes, servRes] = await Promise.all([
          api.get('/reports/dashboard'),
          api.get('/reports/technician-workload'),
          api.get('/reports/service-analysis'),
        ]);
        setMetrics(dashRes.data);
        setTechnicians(techRes.data);
        setServiceAnalysis(servRes.data);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) {
    return <div style={{ textAlign: 'center', padding: '60px', color: 'var(--text-muted)' }}>Loading Executive Analytics...</div>;
  }

  return (
    <div>
      <div style={{ marginBottom: '24px' }}>
        <h1 style={{ fontSize: '1.75rem', fontWeight: 800 }}>Executive Management Dashboard</h1>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginTop: '4px' }}>
          Real-time incident volumes, SLA compliance, and technician workload metrics
        </p>
      </div>

      {/* 5 Summary KPI Cards (FR-017 / Scenario 4) */}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon" style={{ background: 'var(--primary-light)', color: 'var(--primary)' }}>
            <Layers size={24} />
          </div>
          <div>
            <div className="stat-value">{metrics?.totalIncidents ?? 0}</div>
            <div className="stat-label">Total Incidents</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ background: '#fffbeb', color: '#d97706' }}>
            <Clock size={24} />
          </div>
          <div>
            <div className="stat-value">{metrics?.openIncidents ?? 0}</div>
            <div className="stat-label">Open Incidents</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ background: '#fef2f2', color: '#dc2626' }}>
            <AlertOctagon size={24} />
          </div>
          <div>
            <div className="stat-value">{metrics?.criticalIncidents ?? 0}</div>
            <div className="stat-label">Critical (P1)</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ background: '#ecfdf5', color: '#059669' }}>
            <CheckCircle2 size={24} />
          </div>
          <div>
            <div className="stat-value">{metrics?.resolvedIncidents ?? 0}</div>
            <div className="stat-label">Resolved / Closed</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ background: '#fef2f2', color: '#b91c1c' }}>
            <AlertTriangle size={24} />
          </div>
          <div>
            <div className="stat-value">{metrics?.slaViolations ?? 0}</div>
            <div className="stat-label">SLA Violations</div>
          </div>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(400px, 1fr))', gap: '24px' }}>
        {/* Technician Workload Allocation Table */}
        <div className="card">
          <div className="card-header">
            <h3 className="card-title" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <Users size={18} /> Support Technician Workloads
            </h3>
          </div>
          <div className="table-wrapper">
            <table className="table">
              <thead>
                <tr>
                  <th>Technician</th>
                  <th>Skills</th>
                  <th>Active Tickets</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {technicians.map((t) => (
                  <tr key={t.technicianId}>
                    <td>
                      <div style={{ fontWeight: 600 }}>{t.name}</div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{t.email}</div>
                    </td>
                    <td>
                      <div style={{ display: 'flex', gap: '4px', flexWrap: 'wrap' }}>
                        {t.skills.map((s) => (
                          <span key={s} className="badge" style={{ fontSize: '0.65rem', background: 'var(--bg-subtle)' }}>
                            {s}
                          </span>
                        ))}
                      </div>
                    </td>
                    <td>
                      <strong style={{ fontSize: '1rem', color: t.currentWorkload > 3 ? '#dc2626' : 'var(--text-main)' }}>
                        {t.currentWorkload}
                      </strong>
                    </td>
                    <td>
                      {t.isAvailable ? (
                        <span className="badge" style={{ background: '#ecfdf5', color: '#059669' }}>Available</span>
                      ) : (
                        <span className="badge" style={{ background: '#f1f5f9', color: '#64748b' }}>Busy</span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Service Density Analysis */}
        <div className="card">
          <div className="card-header">
            <h3 className="card-title" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <Server size={18} /> IT Service Incident Breakdown
            </h3>
          </div>
          <div className="table-wrapper">
            <table className="table">
              <thead>
                <tr>
                  <th>IT Service</th>
                  <th>Criticality</th>
                  <th>Incidents Logged</th>
                </tr>
              </thead>
              <tbody>
                {serviceAnalysis.map((s) => (
                  <tr key={s.serviceId}>
                    <td style={{ fontWeight: 600 }}>{s.serviceName}</td>
                    <td>
                      <span className="badge" style={{
                        background: s.criticality === 3 ? '#fef2f2' : s.criticality === 2 ? '#fff7ed' : '#f1f5f9',
                        color: s.criticality === 3 ? '#dc2626' : s.criticality === 2 ? '#ea580c' : '#64748b'
                      }}>
                        Level {s.criticality}
                      </span>
                    </td>
                    <td>
                      <strong>{s.totalIncidents}</strong>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
