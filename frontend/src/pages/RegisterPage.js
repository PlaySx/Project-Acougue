import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './RegisterPage.css'; // Importa o CSS

export default function RegisterPage() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [role, setRole] = useState('ROLE_EMPLOYEE');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError('');

        if (!email || !password) {
            setError('Por favor, preencha todos os campos.');
            return;
        }

        const userData = {
            email: email,
            password: password,
            role: role
        };

        try {
            // URL CORRIGIDA para corresponder ao AuthController do backend
            const response = await axios.post('http://localhost:8080/auth/register', userData);

            if (response.status === 201) {
                alert('Usuário cadastrado com sucesso!');
                navigate('/login');
            }
        } catch (err) {
            if (err.response && err.response.data && err.response.data.message) {
                setError(err.response.data.message);
            } else {
                setError('Não foi possível conectar ao servidor.');
            }
        }
    };

    return (
        <div className="register-container">
            <form className="register-form" onSubmit={handleSubmit}>
                <h2>Cadastro de Usuário</h2>

                {error && <p className="error-message">{error}</p>}

                <div className="form-group">
                    <label htmlFor="email">Email:</label>
                    <input
                        type="email"
                        id="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="password">Senha:</label>
                    <input
                        type="password"
                        id="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="role">Função:</label>
                    <select
                        id="role"
                        value={role}
                        onChange={(e) => setRole(e.target.value)}
                    >
                        <option value="ROLE_EMPLOYEE">Funcionário</option>
                        <option value="ROLE_OWNER">Proprietário</option>
                    </select>
                </div>

                <button type="submit" className="register-button">Cadastrar</button>
            </form>
        </div>
    );
}