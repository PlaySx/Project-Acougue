import React, { createContext, useState, useContext, useEffect } from 'react';
import apiClient from '../api/axiosConfig';
import { jwtDecode } from 'jwt-decode';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);

  const setupUserFromToken = (token) => {
    try {
      const decodedToken = jwtDecode(token);
      setUser({ 
        email: decodedToken.sub, 
        role: decodedToken.role,
        establishmentId: decodedToken.establishmentId,
        establishmentName: decodedToken.establishmentName
      });
      setIsAuthenticated(true);
    } catch (error) {
      console.error("Token inválido:", error);
      logout();
    }
  };

  useEffect(() => {
    const token = localStorage.getItem('authToken');
    if (token) {
      try {
        const decodedToken = jwtDecode(token);
        if (decodedToken.exp * 1000 > Date.now()) {
          setupUserFromToken(token);
        } else {
          logout();
        }
      } catch (error) {
        logout();
      }
    }
    setLoading(false);
  }, []);

  // Atualizado para aceitar um token opcional
  const login = async (loginData, token = null) => {
    try {
      if (token) {
        // Caso de registro ou login social: token já fornecido
        localStorage.setItem('authToken', token);
        setupUserFromToken(token);
      } else {
        // Caso de login normal: busca o token
        const response = await apiClient.post('/auth/login', loginData);
        if (response.data && response.data.token) {
          const newToken = response.data.token;
          localStorage.setItem('authToken', newToken);
          setupUserFromToken(newToken);
        }
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
