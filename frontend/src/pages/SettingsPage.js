import React, { useState, useEffect } from 'react';
import apiClient from '../api/axiosConfig';
import { useAuth } from '../context/AuthContext';
import {
  Box, Container, Typography, Alert, TextField, Button, Paper, CircularProgress
} from '@mui/material';

export default function SettingsPage() {
  const { user } = useAuth();
  const [formData, setFormData] = useState({
    name: '',
    address: '',
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    const fetchEstablishment = async () => {
      if (!user?.establishmentId) return;
      try {
        const response = await apiClient.get(`/establishments/${user.establishmentId}`);
        setFormData({
          name: response.data.name,
          address: response.data.address,
        });
      } catch (err) {
        setError('Falha ao carregar dados do estabelecimento.');
      } finally {
        setLoading(false);
      }
    };
    fetchEstablishment();
  }, [user]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    try {
      await apiClient.put(`/establishments/${user.establishmentId}`, formData);
      setSuccess('Dados atualizados com sucesso! Faça login novamente para ver as alterações no topo.');
    } catch (err) {
      setError('Falha ao atualizar os dados.');
    }
  };

  if (user?.role !== 'ROLE_OWNER') {
    return <Alert severity="error" sx={{ mt: 4 }}>Apenas proprietários podem acessar esta página.</Alert>;
  }

  if (loading) return <CircularProgress sx={{ mt: 4 }} />;

  return (
    <Container maxWidth="md">
      <Box sx={{ my: 4 }}>
        <Typography variant="h4" gutterBottom>Configurações do Estabelecimento</Typography>
        
        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}

        <Paper sx={{ p: 3 }}>
          <Box component="form" onSubmit={handleSubmit} noValidate sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
            <TextField
              label="Nome do Estabelecimento"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
              fullWidth
            />
            <TextField
              label="Endereço"
              name="address"
              value={formData.address}
              onChange={handleChange}
              fullWidth
            />
            <Button type="submit" variant="contained" size="large">
              Salvar Alterações
            </Button>
          </Box>
        </Paper>
      </Box>
    </Container>
  );
}
