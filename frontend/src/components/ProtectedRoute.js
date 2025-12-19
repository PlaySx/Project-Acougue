import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

// Este componente envolve as rotas que precisam de autenticação
const ProtectedRoute = ({ children }) => {
  const { isAuthenticated } = useAuth();

  // Se o usuário não estiver autenticado, redireciona para a página de login
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // Se estiver autenticado, renderiza o componente filho (a página solicitada)
  return children;
};

export default ProtectedRoute;
