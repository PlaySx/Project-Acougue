import { createContext, useState, useContext } from 'react';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null); // null = deslogado, objeto = logado

  // Função para simular o login
  const login = (userData) => {
    setUser(userData);
    // No futuro, você também salvaria um token aqui (ex: localStorage)
  };

  // Função para fazer o logout
  const logout = () => {
    setUser(null);
    // No futuro, você removeria o token salvo
  };

  const value = { user, isAuthenticated: !!user, login, logout };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

// Hook customizado para facilitar o uso do contexto
export const useAuth = () => {
  return useContext(AuthContext);
};