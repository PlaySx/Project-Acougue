import React, { useState } from 'react';
import apiClient from '../api/axiosConfig';
import { useAuth } from '../context/AuthContext';
import {
  Box, Button, Container, TextField, Typography, Alert,
  IconButton, Grid, MenuItem, Select, FormControl, InputLabel
} from '@mui/material';
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import RemoveCircleOutlineIcon from '@mui/icons-material/RemoveCircleOutline';

export default function ClientCreatePage() {
  const { user } = useAuth();
  const [formData, setFormData] = useState({
    name: '',
    address: '',
    addressNeighborhood: '',
    observation: '',
    phoneNumbers: [{ type: 'CELULAR', number: '', isPrimary: true }]
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handlePhoneChange = (index, event) => {
    const newPhones = [...formData.phoneNumbers];
    newPhones[index][event.target.name] = event.target.value;
    setFormData(prev => ({ ...prev, phoneNumbers: newPhones }));
  };

  const addPhoneField = () => {
    setFormData(prev => ({
      ...prev,
      phoneNumbers: [...prev.phoneNumbers, { type: 'CELULAR', number: '', isPrimary: false }]
    }));
  };

  const removePhoneField = (index) => {
    const newPhones = formData.phoneNumbers.filter((_, i) => i !== index);
    setFormData(prev => ({ ...prev, phoneNumbers: newPhones }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    setSuccess('');

    if (!user?.establishmentId) {
      setError('Usuário não está associado a um estabelecimento.');
      return;
    }

    const clientData = { ...formData, establishmentId: user.establishmentId };

    try {
      await apiClient.post('/clients', clientData);
      setSuccess('Cliente cadastrado com sucesso!');
      setFormData({ name: '', address: '', addressNeighborhood: '', observation: '', phoneNumbers: [{ type: 'CELULAR', number: '', isPrimary: true }] });
    } catch (err) {
      setError(err.response?.data?.message || 'Ocorreu um erro ao cadastrar o cliente.');
    }
  };

  return (
    <Container maxWidth="md">
      <Box sx={{ my: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>Cadastrar Novo Cliente</Typography>
        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}

        <Box component="form" onSubmit={handleSubmit} noValidate sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
          <TextField label="Nome Completo" value={formData.name} onChange={(e) => setFormData({...formData, name: e.target.value})} required fullWidth />
          
          {formData.phoneNumbers.map((phone, index) => (
            <Grid container spacing={1} key={index} alignItems="center">
              <Grid item xs={4}>
                <FormControl fullWidth><InputLabel>Tipo</InputLabel>
                  <Select name="type" value={phone.type} onChange={(e) => handlePhoneChange(index, e)}><MenuItem value="CELULAR">Celular</MenuItem><MenuItem value="FIXO">Fixo</MenuItem><MenuItem value="WHATSAPP">WhatsApp</MenuItem><MenuItem value="OUTRO">Outro</MenuItem></Select>
                </FormControl>
              </Grid>
              <Grid item xs={7}><TextField fullWidth label="Número" name="number" value={phone.number} onChange={(e) => handlePhoneChange(index, e)} required /></Grid>
              <Grid item xs={1}><IconButton onClick={() => removePhoneField(index)} disabled={formData.phoneNumbers.length === 1}><RemoveCircleOutlineIcon /></IconButton></Grid>
            </Grid>
          ))}
          <Button startIcon={<AddCircleOutlineIcon />} onClick={addPhoneField}>Adicionar Telefone</Button>

          <TextField label="Endereço" value={formData.address} onChange={(e) => setFormData({...formData, address: e.target.value})} required fullWidth />
          <TextField label="Bairro" value={formData.addressNeighborhood} onChange={(e) => setFormData({...formData, addressNeighborhood: e.target.value})} fullWidth />
          <TextField label="Observação" value={formData.observation} onChange={(e) => setFormData({...formData, observation: e.target.value})} fullWidth multiline rows={3} />
          <Button type="submit" variant="contained" color="primary" size="large" sx={{ mt: 3 }}>Salvar Cliente</Button>
        </Box>
      </Box>
    </Container>
  );
}
