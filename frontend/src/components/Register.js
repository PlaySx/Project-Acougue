import React, { useState } from 'react';
import { register } from '../services/api';
import './Register.css';

function Register({ onRegisterSuccess, onBackToLogin }) {
    const [formData, setFormData] = useState({
        username: '',
        password: '',
        confirmPassword: '',
        role: 'ROLE_USER', // Valor padrão
        establishmentId: ''
    });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [loading, setLoading] = useState(false);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
        // Limpa mensagens ao digitar
        setError('');
        setSuccess('');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        // Validações no frontend
        if (formData.password !== formData.confirmPassword) {
            setError('As senhas não coincidem');
            return;
        }

        if (formData.password.length < 6) {
            setError('A senha deve ter no mínimo 6 caracteres');
            return;
        }

        setLoading(true);

        try {
            // Prepara o payload (remove confirmPassword e campos vazios)
            const userData = {
                username: formData.username,
                password: formData.password,
                ...(formData.email && { email: formData.email }),
                ...(formData.establishmentId && { 
                    establishmentId: parseInt(formData.establishmentId) 
                })
            };

            const response = await register(userData);
            console.log('Registro bem-sucedido:', response);
            
            setSuccess('Cadastro realizado com sucesso! Redirecionando para login...');
            
            // Limpa o formulário
            setFormData({
                username: '',
                password: '',
                confirmPassword: '',
                email: '',
                establishmentId: ''
            });

            // Redireciona após 2 segundos
            setTimeout(() => {
                if (onRegisterSuccess) {
                    onRegisterSuccess();
                } else if (onBackToLogin) {
                    onBackToLogin();
                }
            }, 2000);

        } catch (error) {
            console.error('Erro no registro:', error);
            
            if (error.status === 400) {
                setError(error.message || 'Dados inválidos. Verifique os campos.');
            } else if (error.status === 409) {
                setError('Nome de usuário já existe. Escolha outro.');
            } else if (error.status === 500) {
                setError('Erro no servidor. Tente novamente mais tarde.');
            } else if (error.message) {
                setError(error.message);
            } else {
                setError('Erro ao realizar cadastro. Verifique sua conexão.');
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="register-container">
            <form onSubmit={handleSubmit} className="register-form">
                <h2>Cadastro</h2>
                
                {error && <div className="error-message">{error}</div>}
                {success && <div className="success-message">{success}</div>}
                
                <div className="form-group">
                    <label htmlFor="username">Usuário: *</label>
                    <input
                        id="username"
                        name="username"
                        type="text"
                        value={formData.username}
                        onChange={handleChange}
                        placeholder="Digite seu usuário"
                        required
                        disabled={loading}
                        minLength={3}
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="email">Email:</label>
                    <input
                        id="email"
                        name="email"
                        type="email"
                        value={formData.email}
                        onChange={handleChange}
                        placeholder="Digite seu email (opcional)"
                        disabled={loading}
                    />
                </div>
                
                <div className="form-group">
                    <label htmlFor="password">Senha: *</label>
                    <input
                        id="password"
                        name="password"
                        type="password"
                        value={formData.password}
                        onChange={handleChange}
                        placeholder="Mínimo 6 caracteres"
                        required
                        disabled={loading}
                        minLength={6}
                    />
                </div>
                
                <div className="form-group">
                    <label htmlFor="confirmPassword">Confirmar Senha: *</label>
                    <input
                        id="confirmPassword"
                        name="confirmPassword"
                        type="password"
                        value={formData.confirmPassword}
                        onChange={handleChange}
                        placeholder="Digite a senha novamente"
                        required
                        disabled={loading}
                        minLength={6}
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="establishmentId">ID do Estabelecimento:</label>
                    <input
                        id="establishmentId"
                        name="establishmentId"
                        type="number"
                        value={formData.establishmentId}
                        onChange={handleChange}
                        placeholder="ID (opcional)"
                        disabled={loading}
                        min={1}
                    />
                </div>
                
                <button type="submit" disabled={loading}>
                    {loading ? 'Cadastrando...' : 'Cadastrar'}
                </button>

                {onBackToLogin && (
                    <button 
                        type="button" 
                        onClick={onBackToLogin}
                        className="back-button"
                        disabled={loading}
                    >
                        Voltar para Login
                    </button>
                )}
            </form>
        </div>
    );
}

export default Register;