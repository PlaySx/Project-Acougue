import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import apiClient from '../api/axiosConfig';
import {
  Box, Container, Typography, Paper, Grid, Divider, Table, TableBody, TableCell,
  TableContainer, TableHead, TableRow, Chip, Button, CircularProgress, Alert
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import MonetizationOnIcon from '@mui/icons-material/MonetizationOn';
import ShoppingBagIcon from '@mui/icons-material/ShoppingBag';
import AddShoppingCartIcon from '@mui/icons-material/AddShoppingCart'; // Ícone para o novo botão

const StatCard = ({ title, value, icon, color }) => (
  <Paper elevation={2} sx={{ p: 2, display: 'flex', alignItems: 'center', gap: 2 }}>
    <Box sx={{ p: 1, borderRadius: '50%', bgcolor: `${color}.light`, color: `${color}.main` }}>
      {icon}
    </Box>
    <Box>
      <Typography variant="body2" color="text.secondary">{title}</Typography>
      <Typography variant="h6" fontWeight="bold">{value}</Typography>
    </Box>
  </Paper>
);

export default function ClientDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [client, setClient] = useState(null);
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [clientRes, ordersRes] = await Promise.all([
          apiClient.get(`/clients/${id}`),
          apiClient.get(`/clients/${id}/orders`)
        ]);
        setClient(clientRes.data);
        setOrders(ordersRes.data);
      } catch (err) {
        setError('Falha ao carregar dados do cliente.');
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [id]);

  if (loading) return <Box sx={{ display: 'flex', justifyContent: 'center', mt: 5 }}><CircularProgress /></Box>;
  if (error) return <Alert severity="error" sx={{ mt: 2 }}>{error}</Alert>;
  if (!client) return <Typography>Cliente não encontrado.</Typography>;

  const totalSpent = orders.reduce((acc, order) => acc + order.totalValue, 0);
  const lastOrderDate = orders.length > 0 ? new Date(Math.max(...orders.map(o => new Date(o.datahora)))).toLocaleDateString() : 'N/A';

  // Função para navegar para o novo pedido levando o cliente
  const handleNewOrder = () => {
    navigate('/pedidos/novo', { state: { preSelectedClient: client } });
  };

  return (
    <Container maxWidth="lg">
      <Box sx={{ my: 4 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
            <Button startIcon={<ArrowBackIcon />} onClick={() => navigate('/clientes')}>
            Voltar para Lista
            </Button>
            {/* Botão de Ação Principal */}
            <Button 
                variant="contained" 
                color="primary" 
                startIcon={<AddShoppingCartIcon />}
                onClick={handleNewOrder}
            >
                Novo Pedido para este Cliente
            </Button>
        </Box>

        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
          <Typography variant="h4" component="h1">{client.name}</Typography>
          <Chip label={`Cliente desde ${new Date().getFullYear()}`} color="primary" variant="outlined" />
        </Box>

        <Grid container spacing={3} sx={{ mb: 4 }}>
          <Grid item xs={12} md={8}>
            <Paper elevation={3} sx={{ p: 3, height: '100%' }}>
              <Typography variant="h6" gutterBottom>Dados Cadastrais</Typography>
              <Divider sx={{ mb: 2 }} />
              <Grid container spacing={2}>
                <Grid item xs={12} sm={6}>
                  <Typography variant="subtitle2" color="text.secondary">Telefones</Typography>
                  {client.phoneNumbers && client.phoneNumbers.map((p, i) => (
                    <Typography key={i} variant="body1">{p.number} ({p.type})</Typography>
                  ))}
                </Grid>
                <Grid item xs={12} sm={6}>
                  <Typography variant="subtitle2" color="text.secondary">Endereço</Typography>
                  <Typography variant="body1">{client.address}</Typography>
                  <Typography variant="body2" color="text.secondary">{client.addressNeighborhood}</Typography>
                </Grid>
                <Grid item xs={12}>
                  <Typography variant="subtitle2" color="text.secondary">Observações</Typography>
                  <Typography variant="body1" sx={{ fontStyle: 'italic' }}>{client.observation || 'Nenhuma observação.'}</Typography>
                </Grid>
              </Grid>
            </Paper>
          </Grid>
          <Grid item xs={12} md={4}>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
              <StatCard title="Total Gasto" value={`R$ ${totalSpent.toFixed(2)}`} icon={<MonetizationOnIcon />} color="success" />
              <StatCard title="Total de Pedidos" value={orders.length} icon={<ShoppingBagIcon />} color="info" />
              <Paper elevation={2} sx={{ p: 2 }}>
                <Typography variant="body2" color="text.secondary">Última Compra</Typography>
                <Typography variant="h6">{lastOrderDate}</Typography>
              </Paper>
            </Box>
          </Grid>
        </Grid>

        <Typography variant="h5" gutterBottom>Histórico de Pedidos</Typography>
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Data</TableCell>
                <TableCell>Status</TableCell>
                <TableCell>Pagamento</TableCell>
                <TableCell align="right">Valor</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {orders.map((order) => (
                <TableRow key={order.id} hover>
                  <TableCell>#{order.id}</TableCell>
                  <TableCell>{new Date(order.datahora).toLocaleString()}</TableCell>
                  <TableCell>
                    <Chip label={order.status} size="small" color={order.status === 'ENTREGUE' ? 'success' : 'warning'} />
                  </TableCell>
                  <TableCell>{order.paymentMethod}</TableCell>
                  <TableCell align="right">R$ {order.totalValue.toFixed(2)}</TableCell>
                </TableRow>
              ))}
              {orders.length === 0 && (
                <TableRow>
                  <TableCell colSpan={5} align="center">Nenhum pedido encontrado.</TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
      </Box>
    </Container>
  );
}
