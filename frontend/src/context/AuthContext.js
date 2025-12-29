import React, { createContext, useState, useContext, useEffect } from 'react';
import apiClient from '../api/axiosConfig';
import { jwtDecode } from 'jwt-decode';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);

  const setupUserFromToken = (token) => {
    const decodedToken = jwtDecode(token);
    setUser({ 
      email: decodedToken.sub, 
      role: decodedToken.role,
      establishmentId: decodedToken.establishmentId,
      establishmentName: decodedToken.establishmentName // Adicionado
    });
    setIsAuthenticated(true);
  };

  useEffect(() => {
    const token = localStorage.getItem('authToken');
    if (token) {
      try {
        const decodedToken = jwtDecode(token);
        if (decodedToken.exp * 1000 > Date.now()) {
          setupUserFromToken(token);
        } else {
          localStorage.removeItem('authToken');
        }
      } catch (error) {
        console.error("Erro ao decodificar o token:", error);
        localStorage.removeItem('authToken');
      }
    }
    setLoading(false);
  }, []);

  const login = async (loginData) => {
    try {
      const response = await apiClient.post('/auth/login', loginData);
      if (response.data && response.data.token) {
        const { token } = response.data;
        localStorage.setItem('authToken', token);
        setupUserFromToken(token);
      }
    } catch (error) {
      logout();
      throw error;
    }
  };

  const logout = () => {
    localStorage.removeItem('authToken');
    setUser(null);
    setIsAuthenticated(false);
  };

  if (loading) {
    return <div>Carregando...</div>;
  }

  const value = { user, isAuthenticated, login, logout };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export const useAuth = () => {
  return useContext(AuthContext);
};
