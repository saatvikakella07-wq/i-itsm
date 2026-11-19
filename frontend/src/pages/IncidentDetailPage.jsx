import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import {
  ShieldAlert, Clock, CheckCircle2, User, Wrench, AlertTriangle,
  ArrowLeft, Star, MessageSquare, History, Check
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';

export default function IncidentDetailPage() {
  const { id } = useParams();
  const { user, isTechnician, isEmployee, isAdmin } = useAuth();
  const [incident, setIncident] = useState(null);
  const [auditHistory, setAuditHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Modal / Form States
  const [notes, setNotes] = useState('');
  const [showResolveModal, setShowResolveModal] = useState(false);
  const [resolutionDetails, setResolutionDetails] = useState('');
  const [showCloseModal, setShowCloseModal] = useState(false);
  const [feedbackRating, setFeedbackRating] = useState(5);
  const [actionLoading, setActionLoading] = useState(false);

  const fetchDetails = async () => {
    try {
      setLoading(true);
      const res = await api.get(`/incidents/${id}`);
      setIncident(res.data.incident);
      setAuditHistory(res.data.auditHistory || []);
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to load incident details');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDetails();
  }, [id]);

  const handleStatusTransition = async (newStatus, customNotes = '') => {
    try {
      setActionLoading(true);
      setError('');
      await api.patch(`/incidents/${id}/status`, {
        newStatus,
        notes: customNotes || notes,
      });
      setNotes('');
      await fetchDetails();
    } catch (err) {
      setError(err.response?.data?.error || 'Transition failed');
    } finally {
      setActionLoading(false);
    }
  };

  const handleResolve = async (e) => {
    e.preventDefault();
    try {
      setActionLoading(true);
      setError('');
      await api.patch(`/incidents/${id}/resolve`, {
        resolutionDetails,
      });
      setShowResolveModal(false);
      setResolutionDetails('');
      await fetchDetails();
    } catch (err) {
      setError(err.response?.data?.error || 'Resolution failed');
    } finally {
      setActionLoading(false);
    }
  };

  const handleClose = async (e) => {
    e.preventDefault();
    try {
      setActionLoading(true);
      setError('');
      await api.patch(`/incidents/${id}/close`, {
        feedback: Number(feedbackRating),
      });
      setShowCloseModal(false);
      await fetchDetails();
    } catch (err) {
      setError(err.response?.data?.error || 'Closure failed');
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) {
    return <div style={{ textAlign: 'center', padding: '60px', color: 'var(--text-muted)' }}>Loading incident #{id}...</div>;
  }

  if (!incident) {
    return (
      <div className="card" style={{ textAlign: 'center', padding: '40px' }}>
        <h2>Incident not found</h2>
        <Link to="/" className="btn btn-primary" style={{ marginTop: '16px' }}>Return to Dashboard</Link>
      </div>
    );
  }

  const isAssignedTech = isTechnician && incident.technicianId === user.id;
  const isReporter = incident.reporterId === user.id;

  return (
    <div style={{ maxWidth: '960px', margin: '0 auto' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
        <Link to={isAdmin ? '/admin/incidents' : isTechnician ? '/technician' : '/dashboard'} className="btn btn-secondary btn-sm">
          <ArrowLeft size={16} /> Back to List
        </Link>
        <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
          Incident ID: #{incident.id}
        </span>
      </div>

      {/* Duplicate Incident Warning Banner (FR-019 / Task T049) */}
      {incident.potentialDuplicate && (
        <div className="banner banner-warning">
          <AlertTriangle size={20} />
          <div>
            <strong>⚠️ Potential Duplicate Detected:</strong> This incident matches the service and category of active incident{' '}
            {incident.parentIncidentId ? (
              <Link to={`/incidents/${incident.parentIncidentId}`} style={{ fontWeight: 700, color: '#92400e', textDecoration: 'underline' }}>
                #{incident.parentIncidentId}
              </Link>
            ) : 'a previously logged ticket'}{' '}
            reported in the last 24 hours.
          </div>
        </div>
      )}

      {error && (
        <div className="banner banner-danger">
          <AlertTriangle size={18} />
          <span>{error}</span>
        </div>
      )}

      {/* Main Incident Card */}
      <div className="card" style={{ marginBottom: '24px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '16px', marginBottom: '20px' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '6px' }}>
              <h1 style={{ fontSize: '1.5rem', fontWeight: 800 }}>{incident.title}</h1>
              <span className={`badge badge-${incident.priority}`}>{incident.priority}</span>
              <span className={`badge badge-status badge-${incident.status}`}>{incident.status}</span>
              {incident.slaViolated && (
                <span className="badge badge-sla-violated">SLA VIOLATED</span>
              )}
            </div>
            <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
              Ticket <strong>{incident.incidentNumber}</strong> • Reported on{' '}
              {new Date(incident.createdAt).toLocaleString()}
            </div>
          </div>

          {/* Action Buttons Container */}
          <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
            {/* Step 1: Assigned -> Acknowledge */}
            {incident.status === 'ASSIGNED' && (isAssignedTech || isAdmin) && (
              <button
                onClick={() => handleStatusTransition('ACKNOWLEDGED', 'Incident acknowledged by technician')}
                className="btn btn-primary"
                disabled={actionLoading}
              >
                <Check size={16} /> Acknowledge Incident
              </button>
            )}

            {/* Step 2: Acknowledged -> In Progress */}
            {incident.status === 'ACKNOWLEDGED' && (isAssignedTech || isAdmin) && (
              <button
                onClick={() => handleStatusTransition('IN_PROGRESS', 'Investigation initiated')}
                className="btn btn-primary"
                disabled={actionLoading}
              >
                <Wrench size={16} /> Start Investigation
              </button>
            )}

            {/* Step 3: In Progress -> Resolve */}
            {incident.status === 'IN_PROGRESS' && (isAssignedTech || isAdmin) && (
              <button
                onClick={() => setShowResolveModal(true)}
                className="btn btn-success"
                disabled={actionLoading}
              >
                <CheckCircle2 size={16} /> Mark as Resolved
              </button>
            )}

            {/* Step 4: Resolved -> Close */}
            {incident.status === 'RESOLVED' && (isReporter || isAdmin) && (
              <button
                onClick={() => setShowCloseModal(true)}
                className="btn btn-primary"
                disabled={actionLoading}
              >
                <CheckCircle2 size={16} /> Close & Leave Feedback
              </button>
            )}
          </div>
        </div>

        {/* Overview Properties Grid */}
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
          gap: '16px',
          background: 'var(--bg-subtle)',
          padding: '16px',
          borderRadius: 'var(--radius-md)',
          marginBottom: '20px',
        }}>
          <div>
            <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', fontWeight: 600 }}>REPORTER</div>
            <div style={{ fontWeight: 600, marginTop: '2px' }}>{incident.reporterName}</div>
            <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{incident.reporterEmail}</div>
          </div>

          <div>
            <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', fontWeight: 600 }}>ASSIGNED TECHNICIAN</div>
            <div style={{ fontWeight: 600, marginTop: '2px' }}>
              {incident.technicianName || <span style={{ color: 'var(--text-light)' }}>Unassigned</span>}
            </div>
            <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Dynamic Assignment</div>
          </div>

          <div>
            <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', fontWeight: 600 }}>AFFECTED SERVICE</div>
            <div style={{ fontWeight: 600, marginTop: '2px' }}>{incident.serviceName}</div>
            <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
              Criticality: Level {incident.serviceCriticality} / 3
            </div>
          </div>

          <div>
            <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', fontWeight: 600 }}>SLA RESOLUTION TARGET</div>
            <div style={{ fontWeight: 600, marginTop: '2px', color: incident.slaViolated ? '#dc2626' : 'var(--text-main)' }}>
              {incident.slaResolutionDeadline ? new Date(incident.slaResolutionDeadline).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : 'N/A'}
            </div>
            <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
              Impact: {incident.impact}/3 • Urgency: {incident.urgency}/3
            </div>
          </div>
        </div>

        {/* Problem Description */}
        <div style={{ marginBottom: '24px' }}>
          <h3 style={{ fontSize: '1rem', fontWeight: 600, marginBottom: '8px' }}>Problem Description</h3>
          <p style={{ whiteSpace: 'pre-wrap', color: 'var(--text-main)', lineHeight: 1.6 }}>
            {incident.description}
          </p>
        </div>

        {/* Resolution Details (if resolved/closed) */}
        {incident.resolutionDetails && (
          <div style={{
            background: '#ecfdf5',
            border: '1px solid #a7f3d0',
            borderRadius: 'var(--radius-md)',
            padding: '16px',
            marginBottom: '20px',
          }}>
            <h4 style={{ color: '#065f46', display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '6px' }}>
              <CheckCircle2 size={18} /> Resolution Details
            </h4>
            <p style={{ color: '#047857', whiteSpace: 'pre-wrap', fontSize: '0.9rem' }}>
              {incident.resolutionDetails}
            </p>
            {incident.resolvedAt && (
              <div style={{ fontSize: '0.75rem', color: '#059669', marginTop: '6px' }}>
                Resolved at: {new Date(incident.resolvedAt).toLocaleString()}
              </div>
            )}
          </div>
        )}

        {/* Feedback Rating (if closed) */}
        {incident.feedback && (
          <div style={{
            background: '#fffbeb',
            border: '1px solid #fde68a',
            borderRadius: 'var(--radius-md)',
            padding: '14px 16px',
            marginBottom: '20px',
            display: 'flex',
            alignItems: 'center',
            gap: '12px',
          }}>
            <div style={{ fontWeight: 600, fontSize: '0.9rem', color: '#92400e' }}>Employee Rating:</div>
            <div style={{ display: 'flex', gap: '4px', color: '#f59e0b' }}>
              {[1, 2, 3, 4, 5].map((star) => (
                <Star
                  key={star}
                  size={18}
                  fill={star <= incident.feedback ? '#f59e0b' : 'none'}
                />
              ))}
            </div>
          </div>
        )}
      </div>

      {/* Audit Trail Timeline (SC-005: 100% of state changes logged) */}
      <div className="card">
        <h3 style={{ fontSize: '1.1rem', fontWeight: 700, marginBottom: '20px', display: 'flex', alignItems: 'center', gap: '8px' }}>
          <History size={18} /> Immutable Audit History & State Transitions
        </h3>

        <div className="timeline">
          {auditHistory.map((log) => (
            <div key={log.id} className="timeline-item">
              <div className="timeline-dot" />
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', marginBottom: '4px' }}>
                <span style={{ fontWeight: 600, fontSize: '0.9rem' }}>{log.action}</span>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                  {new Date(log.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' })}
                </span>
              </div>
              <div style={{ fontSize: '0.85rem', color: 'var(--text-main)', marginBottom: '4px' }}>
                {log.description}
              </div>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                Actor: <strong>{log.userName}</strong>
                {log.previousStatus && log.newStatus && (
                  <span> • {log.previousStatus} → {log.newStatus}</span>
                )}
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Resolve Incident Modal */}
      {showResolveModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3 style={{ marginBottom: '12px' }}>Resolve Incident</h3>
            <form onSubmit={handleResolve}>
              <div className="form-group">
                <label className="form-label">Resolution Details & Root Cause</label>
                <textarea
                  className="form-control"
                  rows={4}
                  placeholder="Explain how the issue was fixed and any preventative actions taken..."
                  value={resolutionDetails}
                  onChange={(e) => setResolutionDetails(e.target.value)}
                  required
                />
              </div>
              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '16px' }}>
                <button
                  type="button"
                  onClick={() => setShowResolveModal(false)}
                  className="btn btn-secondary"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="btn btn-success"
                  disabled={actionLoading}
                >
                  Confirm Resolution
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Close Incident Modal with Star Rating */}
      {showCloseModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3 style={{ marginBottom: '12px' }}>Close Incident</h3>
            <p style={{ fontSize: '0.88rem', color: 'var(--text-muted)', marginBottom: '16px' }}>
              Confirm that the issue is verified as resolved. Please provide a rating for the support received.
            </p>
            <form onSubmit={handleClose}>
              <div className="form-group" style={{ textAlign: 'center', margin: '20px 0' }}>
                <label className="form-label" style={{ marginBottom: '10px' }}>Resolution Satisfaction</label>
                <div style={{ display: 'flex', justifyContent: 'center', gap: '8px' }}>
                  {[1, 2, 3, 4, 5].map((star) => (
                    <button
                      key={star}
                      type="button"
                      onClick={() => setFeedbackRating(star)}
                      style={{ background: 'none', border: 'none', cursor: 'pointer', padding: '4px' }}
                    >
                      <Star
                        size={28}
                        color="#f59e0b"
                        fill={star <= feedbackRating ? '#f59e0b' : 'none'}
                      />
                    </button>
                  ))}
                </div>
                <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginTop: '8px' }}>
                  {feedbackRating === 5 ? 'Excellent' : feedbackRating === 4 ? 'Good' : feedbackRating === 3 ? 'Average' : 'Needs Improvement'}
                </div>
              </div>
              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '16px' }}>
                <button
                  type="button"
                  onClick={() => setShowCloseModal(false)}
                  className="btn btn-secondary"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="btn btn-primary"
                  disabled={actionLoading}
                >
                  Confirm & Close Ticket
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
