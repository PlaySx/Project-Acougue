import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import apiClient from '../api/axiosConfig';
import { useAuth } from '../context/AuthContext';
import {
  Box, Container, Typography, Alert, Table, TableBody, TableCell,
  TableContainer, TableHead, TableRow, Paper, CircularProgress,
  TextField, Grid, MenuItem, Snackbar, Button
} from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { format } from 'date-fns';
import AddIcon from '@mui/icons-material/Add';

const orderStatusOptions = [
  'PENDENTE', 'CONFIRMADO', 'EM_PREPARO', 'PRONTO', 'A_CAMINHO', 'ENTREGUE', 'CANCELADO'
];

export default function OrderListPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const [filters, setFilters] = useState({
    clientName: '',
    status: '',
    date: null,
  });

  const fetchOrders = useCallback(async () => {
    if (!user?.establishmentId) {
      setError('Usuário não associado a um estabelecimento.');
      setLoading(false);
      return;
    }
    try {
      setLoading(true);
      const params = new URLSearchParams({ establishmentId: user.establishmentId });
      if (filters.clientName) params.append('clientName', filters.clientName);
      if (filters.status) params.append('status', filters.status);
      if (filters.date) params.append('date', format(filters.date, 'yyyy-MM-dd'));
      
      const response = await apiClient.get(`/orders?${params.toString()}`);
      setOrders(response.data);
    } catch (err) {
      setError('Falha ao carregar os pedidos.');
    } finally {
      setLoading(false);
    }
  }, [user, filters]);

  useEffect(() => {
    fetchOrders();
  }, [fetchOrders]);

  const handleFilterChange = (event) => {
    const { name, value } = event.target;
    setFilters(prev => ({ ...prev, [name]: value }));
  };

  const handleDateChange = (newDate) => {
    setFilters(prev => ({ ...prev, date: newDate }));
  };

  const handleStatusChange = async (orderId, newStatus) => {
    try {
      const response = await apiClient.put(`/orders/${orderId}/status`, newStatus, {
        headers: { 'Content-Type': 'text/plain' }
      });
      if (response.status === 200) {
        setOrders(prev => prev.map(o => o.id === orderId ? { ...o, status: newStatus } : o));
        setSuccess(`Status do pedido #${orderId} atualizado!`);
      }
    } catch (err) {
      setError('Falha ao atualizar o status.');
    }
  };

  return (
    <Container maxWidth="lg">
      <Box sx={{ my: 4 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
          <Typography variant="h4" component="h1">Lista de Pedidos</Typography>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => navigate('/pedidos/novo')}
          >
            Novo Pedido
          </Button>
        </Box>
        
        <Paper sx={{ p: 2, mb: 3 }}>
          <Grid container spacing={2} alignItems="center">
            <Grid item xs={12} sm={4}><TextField fullWidth label="Filtrar por nome do cliente" name="clientName" value={filters.clientName} onChange={handleFilterChange} /></Grid>
            <Grid item xs={12} sm={4}><TextField fullWidth select label="Filtrar por status" name="status" value={filters.status} onChange={handleFilterChange}><MenuItem value=""><em>Todos</em></MenuItem>{orderStatusOptions.map(option => <MenuItem key={option} value={option}>{option}</MenuItem>)}</TextField></Grid>
            <Grid item xs={12} sm={4}><DatePicker label="Filtrar por data" value={filters.date} onChange={handleDateChange} renderInput={(params) => <TextField {...params} fullWidth />} /></Grid>
          </Grid>
        </Paper>

        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        <Snackbar open={!!success} autoHideDuration={4000} onClose={() => setSuccess('')} message={success} />

        {loading ? <CircularProgress /> : (
          <TableContainer component={Paper}>
            <Table>
              <TableHead><TableRow><TableCell>ID</TableCell><TableCell>Cliente</TableCell><TableCell>Data</TableCell><TableCell>Status</TableCell><TableCell align="right">Valor Total</TableCell></TableRow></TableHead>
              <TableBody>
                {orders.map((order) => (
                  <TableRow key={order.id}>
                    <TableCell>{order.id}</TableCell>
                    <TableCell>{order.clientName}</TableCell>
                    <TableCell>{new Date(order.datahora).toLocaleString()}</TableCell>
                    <TableCell><TextField select value={order.status} onChange={(e) => handleStatusChange(order.id, e.target.value)} size="small" sx={{ minWidth: 150 }}>{orderStatusOptions.map(option => <MenuItem key={option} value={option}>{option}</MenuItem>)}</TextField></TableCell>
                    <TableCell align="right">R$ {order.totalValue.toFixed(2)}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </Box>
    </Container>
  );
}
