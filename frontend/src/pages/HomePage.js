import React, { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';
import apiClient from '../api/axiosConfig';
import { Grid, Box, Typography, CircularProgress, Alert, Paper } from '@mui/material';
import StatCard from '../components/dashboard/StatCard';
import OrderStatusPieChart from '../components/dashboard/OrderStatusPieChart';
import TopProductsChart from '../components/dashboard/TopProductsChart'; // Importa o novo gráfico
import MonetizationOnIcon from '@mui/icons-material/MonetizationOn';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import PersonAddIcon from '@mui/icons-material/PersonAdd';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';

export default function HomePage() {
  const { user } = useAuth();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchData = useCallback(async () => {
    if (!user?.establishmentId) {
      setError('Nenhum estabelecimento associado a este usuário.');
      setLoading(false);
      return;
    }
    try {
      const response = await apiClient.get(`/dashboard?establishmentId=${user.establishmentId}`);
      setData(response.data);
    } catch (err) {
      setError('Falha ao carregar os dados do dashboard.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, [user]);

  useEffect(() => {
    setLoading(true);
    fetchData();
    const interval = setInterval(fetchData, 30000);
    return () => clearInterval(interval);
  }, [fetchData]);

  if (loading) {
    return <Box sx={{ display: 'flex', justifyContent: 'center', mt: 5 }}><CircularProgress /></Box>;
  }

  if (error) {
    return <Alert severity="error">{error}</Alert>;
  }

  if (!data) {
    return <Typography>Nenhum dado para exibir.</Typography>;
  }

  return (
    <Box>
      <Typography variant="h4" gutterBottom>Dashboard</Typography>
      <Grid container spacing={3}>
        {/* KPIs */}
        <Grid item xs={12} sm={6} md={3}>
          <StatCard title="Faturamento do Dia" value={`R$ ${data.totalRevenueToday.toFixed(2)}`} icon={<MonetizationOnIcon fontSize="large" color="action" />} />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard title="Pedidos Hoje" value={data.totalOrdersToday} icon={<ShoppingCartIcon fontSize="large" color="action" />} />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard title="Ticket Médio Hoje" value={`R$ ${data.averageTicketToday.toFixed(2)}`} icon={<TrendingUpIcon fontSize="large" color="action" />} />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard title="Novos Clientes Hoje" value={data.newClientsToday} icon={<PersonAddIcon fontSize="large" color="action" />} />
        </Grid>

        {/* Gráficos */}
        <Grid item xs={12} md={6}>
          {data.orderStatusCount && Object.keys(data.orderStatusCount).length > 0 ? (
            <OrderStatusPieChart data={data.orderStatusCount} />
          ) : (
            <Paper sx={{ p: 2, textAlign: 'center', height: '100%' }}>
              <Typography>Sem dados de status de pedidos para exibir.</Typography>
            </Paper>
          )}
        </Grid>
        <Grid item xs={12} md={6}>
          {data.topSellingProducts && data.topSellingProducts.length > 0 ? (
            <TopProductsChart data={data.topSellingProducts} />
          ) : (
            <Paper sx={{ p: 2, textAlign: 'center', height: '100%' }}>
              <Typography>Sem dados de produtos vendidos para exibir.</Typography>
            </Paper>
          )}
        </Grid>
      </Grid>
    </Box>
  );
}
