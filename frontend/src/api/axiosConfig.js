import axios from 'axios';

// Cria uma instância do axios que será usada em toda a aplicação
const apiClient = axios.create({
  // CORREÇÃO: Usa a variável de ambiente se existir, senão usa localhost
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080',
});

// Interceptor de Requisição: Adiciona o token a cada chamada
apiClient.interceptors.request.use(
  (config) => {
    // Pega o token do localStorage
    const token = localStorage.getItem('authToken');
    
    // Se o token existir, adiciona ao cabeçalho Authorization
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default apiClient;
