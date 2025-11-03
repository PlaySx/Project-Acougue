import React, { useState } from 'react';
import { login } from '../services/api';
import './Login.css';

function Login({ onLoginSuccess }) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            const response = await login(username, password);
            console.log('Login bem-sucedido:', response);
            console.log('Token:', response.token);
            
            // Token já está salvo automaticamente no localStorage
            
            // Chama a função de callback para atualizar o estado de autenticação
            if (onLoginSuccess) {
                onLoginSuccess();
            }
            
        } catch (error) {
            console.error('Erro no login:', error);
            
            // Trata diferentes tipos de erro
            if (error.status === 401 || error.status === 403) {
                setError('Usuário ou senha inválidos');
            } else if (error.status === 500) {
                setError('Erro no servidor. Tente novamente mais tarde.');
            } else if (error.message) {
                setError(error.message);
            } else {
                setError('Erro ao fazer login. Verifique sua conexão.');
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-container">
            <form onSubmit={handleSubmit} className="login-form">
                <h2>Login</h2>
                
                {error && <div className="error-message">{error}</div>}
                
                <div className="form-group">
                    <label htmlFor="username">Usuário:</label>
                    <input
                        id="username"
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        placeholder="Digite seu usuário"
                        required
                        disabled={loading}
                    />
                </div>
                
                <div className="form-group">
                    <label htmlFor="password">Senha:</label>
                    <input
                        id="password"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Digite sua senha"
                        required
                        disabled={loading}
                    />
                </div>
                
                <button type="submit" disabled={loading}>
                    {loading ? 'Entrando...' : 'Entrar'}
                </button>
            </form>
        </div>
    );
}

export default Login;