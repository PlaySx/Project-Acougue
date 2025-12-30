import React, { useState } from 'react';
import apiClient from '../api/axiosConfig';
import {
  Button, Dialog, DialogActions, DialogContent, DialogTitle,
  TextField, Alert, Box, CircularProgress
} from '@mui/material';

export default function ClientQuickAddDialog({ open, onClose, onClientAdded, establishmentId, initialName }) {
  const [formData, setFormData] = useState({
    name: initialName || '',
    phone: '',
    address: '',
    neighborhood: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async () => {
    if (!formData.name || !formData.phone || !formData.address) {
      setError('Nome, Telefone e Endereço são obrigatórios.');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const clientPayload = {
        name: formData.name,
        address: formData.address,
        addressNeighborhood: formData.neighborhood,
        establishmentId: establishmentId,
        phoneNumbers: [{ type: 'CELULAR', number: formData.phone, isPrimary: true }]
      };

      const response = await apiClient.post('/clients', clientPayload);
      onClientAdded(response.data); // Passa o cliente criado de volta para a página pai
      onClose();
      setFormData({ name: '', phone: '', address: '', neighborhood: '' }); // Limpa
    } catch (err) {
      setError(err.response?.data?.message || 'Erro ao cadastrar cliente.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>Novo Cliente Rápido</DialogTitle>
      <DialogContent>
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
          {error && <Alert severity="error">{error}</Alert>}
          
          <TextField 
            label="Nome" 
            name="name" 
            value={formData.name} 
            onChange={handleChange} 
            fullWidth 
            autoFocus
          />
          <TextField 
            label="Telefone (Celular)" 
            name="phone" 
            value={formData.phone} 
            onChange={handleChange} 
            fullWidth 
            required
          />
          <TextField 
            label="Endereço" 
            name="address" 
            value={formData.address} 
            onChange={handleChange} 
            fullWidth 
            required
          />
          <TextField 
            label="Bairro" 
            name="neighborhood" 
            value={formData.neighborhood} 
            onChange={handleChange} 
            fullWidth 
          />
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancelar</Button>
        <Button onClick={handleSubmit} variant="contained" disabled={loading}>
          {loading ? <CircularProgress size={24} /> : 'Salvar e Selecionar'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
