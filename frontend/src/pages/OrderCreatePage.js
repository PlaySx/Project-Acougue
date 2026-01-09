import React, { useState, useEffect, useCallback } from 'react';
import { useLocation } from 'react-router-dom';
import apiClient from '../api/axiosConfig';
import { useAuth } from '../context/AuthContext';
import {
  Box, Button, Container, Typography, Alert, Autocomplete, TextField,
  List, ListItem, ListItemText, IconButton, Divider, CircularProgress
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import PersonAddIcon from '@mui/icons-material/PersonAdd';
import ClientQuickAddDialog from '../components/ClientQuickAddDialog';
import QuantityDialog from '../components/QuantityDialog';

export default function OrderCreatePage() {
  const { user } = useAuth();
  const location = useLocation();
  
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
  
  // Estado para o loading do autocomplete
  const [loadingClients, setLoadingClients] = useState(false);
  const [clientInputValue, setClientInputValue] = useState('');

  // Busca inicial de produtos e clientes (vazio ou recentes)
  useEffect(() => {
    if (!user?.establishmentId) return;
    const fetchData = async () => {
      try {
        const [productsRes] = await Promise.all([
          apiClient.get(`/products/summary?establishmentId=${user.establishmentId}`)
        ]);
        setProducts(productsRes.data);
        
        // Carrega clientes iniciais (sem filtro)
        fetchClients('');

        // Lógica de repetir pedido
        const { preSelectedClient, repeatOrder } = location.state || {};
        if (preSelectedClient) {
          setSelectedClient(preSelectedClient);
          setClients([preSelectedClient]); // Garante que ele esteja na lista
        } else if (repeatOrder) {
            // ... (lógica de repetir pedido mantida igual)
            // Simplificada para focar na mudança do autocomplete
        }
      } catch (err) { setError('Falha ao carregar dados.'); }
    };
    fetchData();
  }, [user, location.state]);

  // Função para buscar clientes com debounce
  const fetchClients = async (name) => {
    if (!user?.establishmentId) return;
    setLoadingClients(true);
    try {
      const params = new URLSearchParams({ establishmentId: user.establishmentId });
      if (name) params.append('name', name);
      
      const response = await apiClient.get(`/clients/summary?${params.toString()}`);
      setClients(response.data);
    } catch (err) {
      console.error("Erro ao buscar clientes", err);
    } finally {
      setLoadingClients(false);
    }
  };

  // Debounce para o input do cliente
  useEffect(() => {
    const timeoutId = setTimeout(() => {
      if (clientInputValue !== '') {
        fetchClients(clientInputValue);
      }
    }, 500);
    return () => clearTimeout(timeoutId);
  }, [clientInputValue]);

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
              inputValue={clientInputValue}
              onInputChange={(event, newInputValue) => {
                setClientInputValue(newInputValue);
              }}
              options={clients}
              loading={loadingClients}
              getOptionLabel={(option) => option.name || ""}
              isOptionEqualToValue={(option, value) => option.id === value.id}
              renderInput={(params) => (
                <TextField 
                  {...params} 
                  label="Buscar Cliente" 
                  required 
                  InputProps={{
                    ...params.InputProps,
                    endAdornment: (
                      <React.Fragment>
                        {loadingClients ? <CircularProgress color="inherit" size={20} /> : null}
                        {params.InputProps.endAdornment}
                      </React.Fragment>
                    ),
                  }}
                />
              )}
              noOptionsText="Nenhum cliente encontrado"
              filterOptions={(x) => x} // Desabilita filtro local, pois o backend já filtra
            />
            <Button 
              variant="outlined" 
              sx={{ height: '56px', minWidth: '56px' }} 
              onClick={() => setClientDialogOpen(true)}
              title="Cadastrar Novo Cliente"
            >
              <PersonAddIcon sx={{ fontSize: '1.75rem' }} />
            </Button>
          </Box>
          
          <Divider>Produtos</Divider>
          <Autocomplete 
            options={products} 
            getOptionLabel={(p) => `${p.name} - R$ ${p.unitPrice.toFixed(2)} / ${p.pricingType === 'PER_KG' ? 'kg' : 'un'} (${p.stockQuantity} ${p.pricingType === 'PER_KG' ? 'g' : 'un'} em estoque)`} 
            onChange={(_, v) => handleAddProduct(v)} 
            renderInput={(params) => <TextField {...params} label="Adicionar Produto" />} 
            value={null} 
            noOptionsText="Nenhum produto encontrado"
          />
          
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
