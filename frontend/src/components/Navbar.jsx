import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { ShieldAlert, LogOut, PlusCircle, LayoutDashboard, ListChecks, Users } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import NotificationBell from './NotificationBell';

export default function Navbar() {
  const { user, logout, isEmployee, isTechnician, isAdmin } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  if (!user) return null;

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const isActive = (path) => location.pathname === path;

  return (
    <nav className="navbar">
      <div className="nav-container">
        <Link to="/" className="brand">
          <div className="brand-icon">
            <ShieldAlert size={20} />
          </div>
          <span>I-ITSM</span>
        </Link>

        <div className="nav-links">
          {isEmployee && (
            <>
              <Link to="/dashboard" className={`nav-link ${isActive('/dashboard') ? 'active' : ''}`}>
                My Incidents
              </Link>
              <Link to="/incidents/new" className={`nav-link ${isActive('/incidents/new') ? 'active' : ''}`}>
                <PlusCircle size={15} style={{ verticalAlign: 'middle', marginRight: '4px' }} />
                Report Incident
              </Link>
            </>
          )}

          {isTechnician && (
            <Link to="/technician" className={`nav-link ${isActive('/technician') ? 'active' : ''}`}>
              Assigned Queue
            </Link>
          )}

          {isAdmin && (
            <>
              <Link to="/admin" className={`nav-link ${isActive('/admin') ? 'active' : ''}`}>
                <LayoutDashboard size={15} style={{ verticalAlign: 'middle', marginRight: '4px' }} />
                Overview
              </Link>
              <Link to="/admin/incidents" className={`nav-link ${isActive('/admin/incidents') ? 'active' : ''}`}>
                <ListChecks size={15} style={{ verticalAlign: 'middle', marginRight: '4px' }} />
                All Incidents
              </Link>
              <Link to="/admin/users" className={`nav-link ${isActive('/admin/users') ? 'active' : ''}`}>
                <Users size={15} style={{ verticalAlign: 'middle', marginRight: '4px' }} />
                Users
              </Link>
            </>
          )}
        </div>

        <div className="nav-user">
          <NotificationBell />
          <div style={{ textAlign: 'right', fontSize: '0.85rem' }}>
            <div style={{ fontWeight: 600 }}>{user.name}</div>
            <span className="badge" style={{ fontSize: '0.65rem', padding: '1px 6px', background: 'var(--bg-subtle)' }}>
              {user.role}
            </span>
          </div>
          <button
            onClick={handleLogout}
            className="btn btn-secondary btn-sm"
            style={{ padding: '6px 10px' }}
            title="Log out"
          >
            <LogOut size={16} />
          </button>
        </div>
      </div>
    </nav>
  );
}
