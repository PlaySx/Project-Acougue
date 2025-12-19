import React, { useState } from 'react';
import apiClient from '../api/axiosConfig';
import { useAuth } from '../context/AuthContext'; // Importa o hook de autenticação
import {
  Box,
  Button,
  Container,
  TextField,
  Typography,
  Alert,
} from '@mui/material';

export default function ClientCreatePage() {
  const { user } = useAuth(); // Pega os dados do usuário logado
  const [formData, setFormData] = useState({
    name: '',
    numberPhone: '',
    address: '',
    addressNeighborhood: '',
    observation: '',
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    setSuccess('');

    if (!user?.establishmentId) {
      setError('Usuário não está associado a um estabelecimento.');
      return;
    }

    const clientData = {
      ...formData,
      establishmentId: user.establishmentId, // Usa o ID do estabelecimento do usuário logado
    };

    try {
      const response = await apiClient.post('/clients', clientData);
      if (response.status === 201) {
        setSuccess('Cliente cadastrado com sucesso!');
        setFormData({
          name: '',
          numberPhone: '',
          address: '',
          addressNeighborhood: '',
          observation: '',
        });
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Ocorreu um erro ao cadastrar o cliente.');
    }
  };

  return (
    <Container maxWidth="md">
      <Box sx={{ my: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Cadastrar Novo Cliente
        </Typography>

        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}

        <Box component="form" onSubmit={handleSubmit} noValidate>
          <TextField label="Nome Completo" name="name" value={formData.name} onChange={handleChange} variant="outlined" margin="normal" required fullWidth />
          <TextField label="Telefone" name="numberPhone" type="number" value={formData.numberPhone} onChange={handleChange} variant="outlined" margin="normal" required fullWidth />
          <TextField label="Endereço" name="address" value={formData.address} onChange={handleChange} variant="outlined" margin="normal" required fullWidth />
          <TextField label="Bairro" name="addressNeighborhood" value={formData.addressNeighborhood} onChange={handleChange} variant="outlined" margin="normal" fullWidth />
          <TextField label="Observação" name="observation" value={formData.observation} onChange={handleChange} variant="outlined" margin="normal" fullWidth multiline rows={3} />
          <Button type="submit" variant="contained" color="primary" size="large" sx={{ mt: 3 }}>
            Salvar Cliente
          </Button>
        </Box>
      </Box>
    </Container>
  );
}