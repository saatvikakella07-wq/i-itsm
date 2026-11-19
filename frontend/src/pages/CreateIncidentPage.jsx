import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Send, AlertCircle, Sparkles, ArrowLeft } from 'lucide-react';
import api from '../services/api';

const CATEGORIES = [
  'HARDWARE', 'SOFTWARE', 'NETWORK', 'DATABASE',
  'CLOUD', 'SECURITY', 'AUTH', 'APPLICATION', 'STORAGE'
];

export default function CreateIncidentPage() {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [category, setCategory] = useState('APPLICATION');
  const [serviceId, setServiceId] = useState('');
  const [impact, setImpact] = useState(2);
  const [urgency, setUrgency] = useState(2);
  const [services, setServices] = useState([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    api.get('/services')
      .then((res) => {
        setServices(res.data);
        if (res.data.length > 0) {
          setServiceId(res.data[0].id);
        }
      })
      .catch((err) => console.error(err));
  }, []);

  const selectedService = services.find(s => s.id === Number(serviceId));
  const criticality = selectedService ? selectedService.criticality : 1;

  // Real-time calculation of priority matching backend PriorityService
  const score = impact * urgency * criticality;
  const calculatedPriority = score >= 9 ? 'P1' : score >= 5 ? 'P2' : score >= 2 ? 'P3' : 'P4';

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!serviceId) {
      setError('Please select an affected IT service.');
      return;
    }

    setLoading(true);

    try {
      const res = await api.post('/incidents', {
        title: title.trim(),
        description: description.trim(),
        category,
        serviceId: Number(serviceId),
        impact: Number(impact),
        urgency: Number(urgency),
      });
      navigate(`/incidents/${res.data.id}`);
    } catch (err) {
      if (err.response?.data?.details) {
        const detailMessages = Object.values(err.response.data.details).join(' • ');
        setError(detailMessages || err.response.data.error || 'Validation failed');
      } else {
        setError(err.response?.data?.error || 'Failed to submit incident');
      }
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '680px', margin: '0 auto' }}>
      <button
        onClick={() => navigate(-1)}
        className="btn btn-secondary btn-sm"
        style={{ marginBottom: '16px' }}
      >
        <ArrowLeft size={16} /> Back
      </button>

      <div className="card">
        <div style={{ marginBottom: '24px' }}>
          <h1 style={{ fontSize: '1.5rem', fontWeight: 800 }}>Report an IT Incident</h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginTop: '4px' }}>
            Describe the technical disruption so our intelligent routing engine can assign the right technician
          </p>
        </div>

        {error && (
          <div className="banner banner-danger">
            <AlertCircle size={18} />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Incident Title</label>
            <input
              type="text"
              className="form-control"
              placeholder="e.g. Payment application unavailable for transactions"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              required
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
            <div className="form-group">
              <label className="form-label">Operational Category</label>
              <select
                className="form-control"
                value={category}
                onChange={(e) => setCategory(e.target.value)}
              >
                {CATEGORIES.map(c => (
                  <option key={c} value={c}>{c}</option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label className="form-label">Affected IT Service</label>
              <select
                className="form-control"
                value={serviceId}
                onChange={(e) => setServiceId(e.target.value)}
                required
              >
                {services.map(s => (
                  <option key={s.id} value={s.id}>
                    {s.name} (Criticality: {s.criticality === 3 ? 'High' : s.criticality === 2 ? 'Med' : 'Low'})
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px', margin: '8px 0 16px' }}>
            <div className="form-group">
              <label className="form-label">
                Business Impact: <strong style={{ color: 'var(--primary)' }}>{impact} / 3</strong>
              </label>
              <input
                type="range"
                min="1"
                max="3"
                value={impact}
                onChange={(e) => setImpact(Number(e.target.value))}
                style={{ width: '100%' }}
              />
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.72rem', color: 'var(--text-muted)' }}>
                <span>1 - Minor</span>
                <span>2 - Moderate</span>
                <span>3 - Severe</span>
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">
                Time Urgency: <strong style={{ color: 'var(--primary)' }}>{urgency} / 3</strong>
              </label>
              <input
                type="range"
                min="1"
                max="3"
                value={urgency}
                onChange={(e) => setUrgency(Number(e.target.value))}
                style={{ width: '100%' }}
              />
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.72rem', color: 'var(--text-muted)' }}>
                <span>1 - Can wait</span>
                <span>2 - Normal</span>
                <span>3 - Immediate</span>
              </div>
            </div>
          </div>

          {/* Intelligent Priority Determination Box */}
          <div style={{
            background: 'var(--bg-subtle)',
            border: '1px solid var(--border-color)',
            borderRadius: 'var(--radius-md)',
            padding: '16px',
            marginBottom: '20px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between'
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <div style={{ color: 'var(--primary)' }}><Sparkles size={20} /></div>
              <div>
                <div style={{ fontSize: '0.85rem', fontWeight: 600 }}>Calculated Priority Preview</div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                  Score = Impact ({impact}) × Urgency ({urgency}) × Criticality ({criticality}) = {score}
                </div>
              </div>
            </div>
            <span className={`badge badge-${calculatedPriority}`} style={{ fontSize: '0.9rem', padding: '6px 14px' }}>
              {calculatedPriority}
            </span>
          </div>

          <div className="form-group">
            <label className="form-label">Detailed Problem Description</label>
            <textarea
              className="form-control"
              rows={4}
              placeholder="Provide exact error messages, steps to reproduce, or affected users..."
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              required
            />
          </div>

          <button
            type="submit"
            className="btn btn-primary"
            style={{ width: '100%', padding: '12px' }}
            disabled={loading}
          >
            <Send size={18} />
            {loading ? 'Submitting & Routing...' : 'Submit Incident'}
          </button>
        </form>
      </div>
    </div>
  );
}
