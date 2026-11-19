import React, { createContext, useState, useEffect } from 'react';
import api from '../services/api';

export const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try {
      const cached = localStorage.getItem('itsm_user');
      return cached ? JSON.parse(cached) : null;
    } catch (e) {
      return null;
    }
  });
  const [token, setToken] = useState(() => localStorage.getItem('itsm_token'));
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (token) {
      api.get('/users/me')
        .then((res) => {
          const userData = { ...res.data, role: res.data.role };
          setUser(userData);
          localStorage.setItem('itsm_user', JSON.stringify(userData));
        })
        .catch((err) => {
          if (err.response && err.response.status === 401) {
            logout();
          }
        })
        .finally(() => setLoading(false));
    } else {
      setLoading(false);
    }
  }, [token]);

  const login = async (email, password) => {
    const res = await api.post('/auth/login', { email, password });
    const { token: jwtToken, userId, name, role } = res.data;
    const userData = { id: userId, email, name, role };
    
    setToken(jwtToken);
    setUser(userData);
    localStorage.setItem('itsm_token', jwtToken);
    localStorage.setItem('itsm_user', JSON.stringify(userData));
    return userData;
  };

  const register = async (name, email, password, role) => {
    return await api.post('/auth/register', { name, email, password, role });
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('itsm_token');
    localStorage.removeItem('itsm_user');
  };

  const isEmployee = user?.role === 'EMPLOYEE';
  const isTechnician = user?.role === 'TECHNICIAN';
  const isAdmin = user?.role === 'ADMIN';

  return (
    <AuthContext.Provider value={{ user, token, loading, login, register, logout, isEmployee, isTechnician, isAdmin }}>
      {children}
    </AuthContext.Provider>
  );
}

export { useAuth } from './useAuth';
