import React, { useState, useEffect, useCallback } from 'react';
import { useLocation } from 'react-router-dom'; // 1. Importa o useLocation
import apiClient from '../api/axiosConfig';
import { useAuth } from '../context/AuthContext';
import {
  Box, Button, Container, Typography, Alert, Autocomplete, TextField,
  List, ListItem, ListItemText, IconButton, Divider
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import PersonAddIcon from '@mui/icons-material/PersonAdd';
import ClientQuickAddDialog from '../components/ClientQuickAddDialog';
import QuantityDialog from '../components/QuantityDialog';

export default function OrderCreatePage() {
  const { user } = useAuth();
  const location = useLocation(); // 2. Usa o hook para acessar o state da navegação

  // 3. Verifica se um cliente foi pré-selecionado e o usa como estado inicial
  const [selectedClient, setSelectedClient] = useState(location.state?.preSelectedClient || null);
  
  const [orderItems, setOrderItems] = useState([]);
  const [paymentMethod, setPaymentMethod] = useState('Dinheiro');
  const [observation, setObservation] = useState('');
  
  const [clients, setClients] = useState([]);
  const [products, setProducts] = useState([]);
  
  const [quantityDialogOpen, setQuantityDialogOpen] = useState(false);
  const [productForDialog, setProductForDialog] = useState(null);
  
  const [clientDialogOpen, setClientDialogOpen] = useState(false);

  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const fetchClients = useCallback(async () => {
    if (!user?.establishmentId) return;
    try {
      const response = await apiClient.get(`/clients/advanced-search?establishmentId=${user.establishmentId}`);
      setClients(response.data);
    } catch (err) {
      setError('Falha ao carregar clientes.');
    }
  }, [user]);

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
      setQuantityDialogOpen(true);
    }
  };

  const handleQuantitySubmit = (values) => {
    const { weightInGrams, quantity } = values;
    const price = (productForDialog.unitPrice * (weightInGrams ? weightInGrams / 1000 : quantity));
    setOrderItems([...orderItems, { productId: productForDialog.id, name: productForDialog.name, weightInGrams, quantity, price: parseFloat(price.toFixed(2)) }]);
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
          
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <Autocomplete
              sx={{ flexGrow: 1 }}
              value={selectedClient}
              onChange={(event, newValue) => {
                setSelectedClient(newValue);
              }}
              options={clients}
              getOptionLabel={(option) => option.name || ""}
              isOptionEqualToValue={(option, value) => option.id === value.id}
              renderInput={(params) => <TextField {...params} label="Buscar Cliente" required />}
            />
            <Button 
              variant="outlined" 
              sx={{ height: '56px', minWidth: '56px' }} 
              onClick={() => setClientDialogOpen(true)}
              title="Cadastrar Novo Cliente"
            >
              <PersonAddIcon />
            </Button>
          </Box>
          
          <Divider>Produtos</Divider>
          <Autocomplete options={products} getOptionLabel={(p) => `${p.name} - R$ ${p.unitPrice.toFixed(2)} / ${p.pricingType === 'PER_KG' ? 'kg' : 'un'} (${p.stockQuantity} ${p.pricingType === 'PER_KG' ? 'g' : 'un'} em estoque)`} onChange={(_, v) => handleAddProduct(v)} renderInput={(params) => <TextField {...params} label="Adicionar Produto" />} value={null} />
          
          <List>
            {orderItems.map((item, index) => (
              <ListItem key={`${item.productId}-${index}`} secondaryAction={<IconButton edge="end" onClick={() => setOrderItems(orderItems.filter((_, i) => i !== index))}><DeleteIcon /></IconButton>}>
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
      
      {productForDialog && <QuantityDialog open={quantityDialogOpen} onClose={() => setQuantityDialogOpen(false)} onSubmit={handleQuantitySubmit} product={productForDialog} />}
      
      <ClientQuickAddDialog
        open={clientDialogOpen}
        onClose={() => setClientDialogOpen(false)}
        onClientAdded={(newClient) => {
          setClients(prev => [...prev, newClient]);
          setSelectedClient(newClient);
        }}
        establishmentId={user?.establishmentId}
        initialName=""
      />
    </Container>
  );
}
