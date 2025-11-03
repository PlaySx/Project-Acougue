import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080'
});

export const login = async (username, password) => {
    try {
        const response = await api.post('/auth/login', { username, password });

        // O backend pode retornar o token com nomes diferentes: token, accessToken, jwt, etc.
        const data = response.data || {};
        const token = data.token || data.accessToken || data.jwt || data?.data?.token;

        if (token) {
            localStorage.setItem('token', token);
            api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
        }

        return data;
    } catch (error) {
        // normalize the error to include status and message
        if (error.response) {
            const errData = error.response.data || {};
            const message = errData.message || errData.error || JSON.stringify(errData);
            const err = new Error(message);
            err.status = error.response.status;
            throw err;
        }
        throw new Error(error.message || 'Erro desconhecido');
    }
};

export const logout = () => {
    localStorage.removeItem('token');
    delete api.defaults.headers.common['Authorization'];
};

export const register = async (username, password, role, establishmentId) => {
    try {
        const payload = { username, password, role, establishmentId };
        const response = await api.post('/users/register', payload);
        return response.data;
    } catch (error) {
        throw error.response ? error.response.data : error.message;
    }
};

export default api;