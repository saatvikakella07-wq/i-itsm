import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { ShieldAlert, LogIn, AlertCircle } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export default function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const isExpired = new URLSearchParams(location.search).get('expired');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const user = await login(email, password);
      if (user.role === 'ADMIN') navigate('/admin');
      else if (user.role === 'TECHNICIAN') navigate('/technician');
      else navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to authenticate. Please check your credentials.');
    } finally {
      setLoading(false);
    }
  };

  const fillQuickLogin = (quickEmail, quickPassword) => {
    setEmail(quickEmail);
    setPassword(quickPassword);
  };

  return (
    <div style={{ maxWidth: '440px', margin: '40px auto', padding: '0 16px' }}>
      <div style={{ textAlign: 'center', marginBottom: '28px' }}>
        <div style={{
          width: '56px',
          height: '56px',
          background: 'linear-gradient(135deg, #2563eb, #1d4ed8)',
          color: 'white',
          borderRadius: 'var(--radius-md)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          margin: '0 auto 16px',
          boxShadow: 'var(--shadow-md)',
        }}>
          <ShieldAlert size={32} />
        </div>
        <h1 style={{ fontSize: '1.6rem', fontWeight: 800, color: 'var(--text-main)' }}>Sign in to I-ITSM</h1>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginTop: '6px' }}>
          Intelligent IT Service Management & Incident Resolution
        </p>
      </div>

      <div className="card">
        {isExpired && (
          <div className="banner banner-warning">
            Your session has expired. Please log in again.
          </div>
        )}

        {error && (
          <div className="banner banner-danger">
            <AlertCircle size={18} />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Email Address</label>
            <input
              type="email"
              className="form-control"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="e.g. alice@org.com"
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Password</label>
            <input
              type="password"
              className="form-control"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              required
            />
          </div>

          <button
            type="submit"
            className="btn btn-primary"
            style={{ width: '100%', marginTop: '8px' }}
            disabled={loading}
          >
            <LogIn size={18} />
            {loading ? 'Authenticating...' : 'Sign In'}
          </button>
        </form>

        <div style={{ margin: '24px 0 16px', textAlign: 'center', position: 'relative' }}>
          <hr style={{ border: 'none', borderTop: '1px solid var(--border-color)' }} />
          <span style={{
            position: 'absolute',
            top: '-10px',
            left: '50%',
            transform: 'translateX(-50%)',
            background: 'white',
            padding: '0 12px',
            fontSize: '0.75rem',
            color: 'var(--text-muted)',
            fontWeight: 600,
          }}>
            QUICK DEMO ACCOUNTS
          </span>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '8px' }}>
          <button
            type="button"
            onClick={() => fillQuickLogin('emp@org.com', 'emp123')}
            className="btn btn-secondary btn-sm"
            style={{ fontSize: '0.75rem' }}
          >
            Employee
          </button>
          <button
            type="button"
            onClick={() => fillQuickLogin('tech@org.com', 'tech123')}
            className="btn btn-secondary btn-sm"
            style={{ fontSize: '0.75rem' }}
          >
            Technician
          </button>
          <button
            type="button"
            onClick={() => fillQuickLogin('admin@org.com', 'admin123')}
            className="btn btn-secondary btn-sm"
            style={{ fontSize: '0.75rem' }}
          >
            Admin
          </button>
        </div>

        <div style={{ marginTop: '20px', textAlign: 'center', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
          Don't have an account?{' '}
          <Link to="/register" style={{ color: 'var(--primary)', fontWeight: 600, textDecoration: 'none' }}>
            Register here
          </Link>
        </div>
      </div>
    </div>
  );
}
