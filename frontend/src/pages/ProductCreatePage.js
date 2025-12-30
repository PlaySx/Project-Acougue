import React, { useState } from 'react';
import apiClient from '../api/axiosConfig';
import { useAuth } from '../context/AuthContext';
import { Box, Button, Container, TextField, Typography, Alert, MenuItem, FormControl, FormLabel } from '@mui/material';

const categories = ['CARNES', 'BEBIDAS', 'MERCEARIA', 'PADARIA', 'FRIOS_E_LATICINIOS', 'HORTIFRUTI', 'OUTROS'];
const pricingTypes = ['PER_KG', 'PER_UNIT'];

export default function ProductCreatePage() {
  const { user } = useAuth();
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    category: 'CARNES',
    pricingType: 'PER_KG',
    unitPrice: '',
    stockQuantity: 0,
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    setSuccess('');

    if (!user?.establishmentId) {
      setError('Usuário não está associado a um estabelecimento.');
      return;
    }

    // LÓGICA DE CONVERSÃO: Converte KG para Gramas antes de enviar
    const stockValueToSave = formData.pricingType === 'PER_KG'
      ? Math.round(parseFloat(formData.stockQuantity) * 1000)
      : parseInt(formData.stockQuantity, 10);

    const productData = { 
      ...formData, 
      stockQuantity: stockValueToSave,
      establishmentId: user.establishmentId 
    };

    try {
      const response = await apiClient.post('/products', productData);
      if (response.status === 201) {
        setSuccess('Produto cadastrado com sucesso!');
        setFormData({ name: '', description: '', category: 'CARNES', pricingType: 'PER_KG', unitPrice: '', stockQuantity: 0 });
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Ocorreu um erro ao cadastrar o produto.');
    }
  };

  return (
    <Container maxWidth="md">
      <Box sx={{ my: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>Cadastrar Novo Produto</Typography>
        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}

        <Box component="form" onSubmit={handleSubmit} noValidate sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
          <TextField label="Nome do Produto" name="name" value={formData.name} onChange={handleChange} required fullWidth />
          <TextField label="Descrição" name="description" value={formData.description} onChange={handleChange} fullWidth multiline rows={3} />
          <FormControl fullWidth>
            <FormLabel>Categoria</FormLabel>
            <TextField name="category" value={formData.category} onChange={handleChange} select fullWidth>
              {categories.map(c => <MenuItem key={c} value={c}>{c}</MenuItem>)}
            </TextField>
          </FormControl>
          <FormControl fullWidth>
            <FormLabel>Tipo de Preço</FormLabel>
            <TextField name="pricingType" value={formData.pricingType} onChange={handleChange} select fullWidth>
              {pricingTypes.map(p => <MenuItem key={p} value={p}>{p === 'PER_KG' ? 'Por Quilo (KG)' : 'Por Unidade'}</MenuItem>)}
            </TextField>
          </FormControl>
          <TextField label={formData.pricingType === 'PER_KG' ? 'Preço por KG (R$)' : 'Preço da Unidade (R$)'} name="unitPrice" type="number" value={formData.unitPrice} onChange={handleChange} required fullWidth />
          {/* Label dinâmico para o estoque */}
          <TextField label={formData.pricingType === 'PER_KG' ? 'Estoque (em kg)' : 'Estoque (unidades)'} name="stockQuantity" type="number" value={formData.stockQuantity} onChange={handleChange} required fullWidth />
          <Button type="submit" variant="contained" color="primary" size="large" sx={{ mt: 2 }}>Salvar Produto</Button>
        </Box>
      </Box>
    </Container>
  );
}
