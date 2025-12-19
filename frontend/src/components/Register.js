import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './Register.css'; // Vamos criar este arquivo de CSS a seguir

function Register() {
    // Estados para armazenar os dados do formulário
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [role, setRole] = useState('ROLE_EMPLOYEE'); // Valor padrão
    const [error, setError] = useState('');
    const navigate = useNavigate(); // Hook para redirecionar o usuário

    // Função chamada ao submeter o formulário
    const handleSubmit = async (event) => {
        event.preventDefault(); // Previne o recarregamento da página
        setError(''); // Limpa erros anteriores

        if (!username || !password) {
            setError('Por favor, preencha todos os campos.');
            return;
        }

        // Monta o objeto para enviar ao backend
        const userData = {
            username: username,
            password: password,
            role: role
        };

        try {
            // Faz a requisição POST para o endpoint do backend
            const response = await axios.post('http://localhost:8080/users', userData);

            // Se a resposta for 201 Created...
            if (response.status === 201) {
                alert('Usuário cadastrado com sucesso!');
                navigate('/login'); // Redireciona para a tela de login
            }
        } catch (err) {
            // Se o backend retornar um erro (ex: username já existe)
            if (err.response && err.response.data) {
                // A mensagem de erro que definimos no backend
                setError(err.response.data.message || 'Erro ao cadastrar. Tente novamente.');
            } else {
                setError('Não foi possível conectar ao servidor. Verifique sua conexão.');
            }
        }
    };

    return (
        <div className="register-container">
            <form className="register-form" onSubmit={handleSubmit}>
                <h2>Cadastro de Usuário</h2>

                {error && <p className="error-message">{error}</p>}

                <div className="form-group">
                    <label htmlFor="username">Usuário:</label>
                    <input
                        type="text"
                        id="username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
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

export default Register;