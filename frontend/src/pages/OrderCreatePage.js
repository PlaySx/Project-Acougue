import React, { useState, useEffect } from 'react';
import apiClient from '../api/axiosConfig';
import { useAuth } from '../context/AuthContext';
import {
  Box, Button, Container, Typography, Alert, Autocomplete, TextField,
  List, ListItem, ListItemText, IconButton, Divider, Dialog, DialogActions,
  DialogContent, DialogTitle
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';

function QuantityDialog({ open, onClose, onSubmit, product }) {
  const [value, setValue] = useState('');
  const isPerKg = product?.pricingType === 'PER_KG';
  const label = isPerKg ? "Peso em gramas (g)" : "Quantidade (unidades)";

  const handleSubmit = () => {
    const numValue = parseInt(value, 10);
    if (numValue > 0) {
      onSubmit(isPerKg ? { weightInGrams: numValue } : { quantity: numValue });
      onClose();
      setValue('');
    }
  };

  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>Adicionar {product?.name}</DialogTitle>
      <DialogContent>
        <TextField autoFocus margin="dense" label={label} type="number" fullWidth variant="standard" value={value} onChange={(e) => setValue(e.target.value)} />
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancelar</Button>
        <Button onClick={handleSubmit}>Adicionar</Button>
      </DialogActions>
    </Dialog>
  );
}

export default function OrderCreatePage() {
  const { user } = useAuth();
  const [selectedClient, setSelectedClient] = useState(null);
  const [orderItems, setOrderItems] = useState([]);
  const [paymentMethod, setPaymentMethod] = useState('Dinheiro');
  const [observation, setObservation] = useState('');
  
  const [clients, setClients] = useState([]);
  const [products, setProducts] = useState([]);
  
  const [dialogOpen, setDialogOpen] = useState(false);
  const [productForDialog, setProductForDialog] = useState(null);

  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    if (!user?.establishmentId) return;
    const fetchData = async () => {
      try {
        const [clientsRes, productsRes] = await Promise.all([
          apiClient.get(`/clients/advanced-search?establishmentId=${user.establishmentId}`),
          apiClient.get(`/products?establishmentId=${user.establishmentId}`)
        ]);
        setClients(clientsRes.data);
        setProducts(productsRes.data);
      } catch (err) { setError('Falha ao carregar dados.'); }
    };
    fetchData();
  }, [user]);

  const handleAddProduct = (product) => {
    if (product) {
      setProductForDialog(product);
      setDialogOpen(true);
    }
  };

  const handleQuantitySubmit = (values) => {
    const { weightInGrams, quantity } = values;
    const price = (productForDialog.unitPrice * (weightInGrams ? weightInGrams / 1000 : quantity));

    setOrderItems([...orderItems, {
      productId: productForDialog.id,
      name: productForDialog.name,
      weightInGrams,
      quantity,
      price: parseFloat(price.toFixed(2)),
    }]);
  };

  const calculateTotal = () => orderItems.reduce((total, item) => total + item.price, 0).toFixed(2);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError(''); setSuccess('');

    if (!selectedClient || orderItems.length === 0) {
      setError('Selecione um cliente e adicione pelo menos um produto.');
      return;
    }

    const orderData = {
      clientId: selectedClient.id,
      items: orderItems.map(({ productId, weightInGrams, quantity }) => ({ productId, weightInGrams, quantity })),
      paymentMethod,
      observation,
      establishmentId: user.establishmentId,
    };

    try {
      await apiClient.post('/orders', orderData);
      setSuccess('Pedido criado com sucesso!');
      setSelectedClient(null);
      setOrderItems([]);
    } catch (err) {
      setError(err.response?.data?.message || 'Ocorreu um erro ao criar o pedido.');
    }
  };

  return (
    <Container maxWidth="md">
      <Box sx={{ my: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>Criar Novo Pedido</Typography>
        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}

        <Box component="form" onSubmit={handleSubmit} noValidate sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
          <Autocomplete options={clients} getOptionLabel={(c) => c.name || ''} value={selectedClient} onChange={(_, v) => setSelectedClient(v)} renderInput={(params) => <TextField {...params} label="Selecione um Cliente" required />} />
          <Divider>Produtos</Divider>
          <Autocomplete options={products} getOptionLabel={(p) => `${p.name} - R$ ${p.unitPrice.toFixed(2)} / ${p.pricingType === 'PER_KG' ? 'kg' : 'un'} (${p.stockQuantity} ${p.pricingType === 'PER_KG' ? 'g' : 'un'} em estoque)`} onChange={(_, v) => handleAddProduct(v)} renderInput={(params) => <TextField {...params} label="Adicionar Produto" />} value={null} />
          
          <List>
            {orderItems.map((item) => (
              <ListItem key={item.productId} secondaryAction={<IconButton edge="end" onClick={() => setOrderItems(orderItems.filter(i => i.productId !== item.productId))}><DeleteIcon /></IconButton>}>
                <ListItemText primary={item.name} secondary={`${item.quantity ? `${item.quantity} un` : `${item.weightInGrams}g`} - R$ ${item.price.toFixed(2)}`} />
              </ListItem>
            ))}
          </List>

          {orderItems.length > 0 && <Typography variant="h6" align="right">Total: R$ {calculateTotal()}</Typography>}
          
          <TextField label="Método de Pagamento" name="paymentMethod" value={paymentMethod} onChange={(e) => setPaymentMethod(e.target.value)} variant="outlined" fullWidth />
          <TextField label="Observação" name="observation" value={observation} onChange={(e) => setObservation(e.target.value)} variant="outlined" fullWidth multiline rows={3} />
          <Button type="submit" variant="contained" color="primary" size="large" sx={{ mt: 2 }}>Finalizar Pedido</Button>
        </Box>
      </Box>
      {productForDialog && <QuantityDialog open={dialogOpen} onClose={() => setDialogOpen(false)} onSubmit={handleQuantitySubmit} product={productForDialog} />}
    </Container>
  );
}
