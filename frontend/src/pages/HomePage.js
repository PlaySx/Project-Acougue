import React, { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';
import apiClient from '../api/axiosConfig';
import { Grid, Box, Typography, CircularProgress, Alert, Paper, useTheme, TextField } from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { format, subDays } from 'date-fns';
import Chart from 'react-apexcharts';

import MonetizationOnIcon from '@mui/icons-material/MonetizationOn';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import PersonAddIcon from '@mui/icons-material/PersonAdd';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';

import RecentOrders from '../components/dashboard/RecentOrders';

const StatCard = ({ title, value, icon, series }) => {
  const theme = useTheme();
  const options = {
    chart: { type: 'area', sparkline: { enabled: true } },
    stroke: { curve: 'smooth', width: 2 },
    fill: { type: 'gradient', gradient: { shadeIntensity: 1, opacityFrom: 0.4, opacityTo: 0.1 } },
    tooltip: { enabled: false },
    colors: [theme.palette.primary.main],
  };

  return (
    <Paper elevation={3} sx={{ p: 2, display: 'flex', flexDirection: 'column', height: '100%' }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start' }}>
        <Typography color="text.secondary" sx={{ fontSize: '0.875rem' }}>{title}</Typography>
        {icon}
      </Box>
      <Typography variant="h5" sx={{ mt: 1, mb: 1 }}>{value}</Typography>
      <Box sx={{ flexGrow: 1, minHeight: '40px' }}>
        <Chart options={options} series={series} type="area" height="100%" />
      </Box>
    </Paper>
  );
};

export default function HomePage() {
  const { user } = useAuth();
  const theme = useTheme();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  // Estado para o filtro de data (Padrão: Últimos 7 dias)
  const [dateRange, setDateRange] = useState({
    startDate: subDays(new Date(), 6),
    endDate: new Date()
  });

  const fetchData = useCallback(async () => {
    if (!user?.establishmentId) return;
    try {
      setLoading(true);
      const params = new URLSearchParams({ 
        establishmentId: user.establishmentId,
        startDate: format(dateRange.startDate, 'yyyy-MM-dd'),
        endDate: format(dateRange.endDate, 'yyyy-MM-dd')
      });
      const response = await apiClient.get(`/dashboard?${params.toString()}`);
      setData(response.data);
    } catch (err) {
      setError('Falha ao carregar dados do dashboard.');
    } finally {
      setLoading(false);
    }
  }, [user, dateRange]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const chartBaseOptions = {
    chart: { background: 'transparent', toolbar: { show: false }, zoom: { enabled: false } },
    theme: { mode: theme.palette.mode },
    dataLabels: { enabled: false },
    grid: { show: true, borderColor: theme.palette.divider, strokeDashArray: 4 },
  };

  const lineOptions = { 
    ...chartBaseOptions, 
    stroke: { curve: 'smooth', width: 3 }, 
    xaxis: { 
      type: 'datetime', 
      categories: data?.weeklyRevenue ? data.weeklyRevenue.map(d => d.date) : [],
      labels: { format: 'dd/MM' },
      tooltip: { enabled: false }
    }, 
    title: { text: 'Faturamento no Período', align: 'left', style: { fontSize: '16px', fontWeight: 500 } }, 
    tooltip: { x: { format: 'dd/MM/yyyy' } },
    colors: [theme.palette.primary.main]
  };
  const lineSeries = [{ name: 'Faturamento', data: data?.weeklyRevenue ? data.weeklyRevenue.map(d => d.revenue) : [] }];

  const barOptions = { 
    ...chartBaseOptions, 
    plotOptions: { bar: { borderRadius: 4, horizontal: true, barHeight: '70%' } }, 
    xaxis: { categories: data?.topSellingProducts ? data.topSellingProducts.map(p => p.productName) : [], labels: { show: false } }, 
    title: { text: 'Top Produtos no Período', align: 'left', style: { fontSize: '16px', fontWeight: 500 } },
    colors: [theme.palette.primary.light]
  };
  const barSeries = [{ name: 'Vendas', data: data?.topSellingProducts ? data.topSellingProducts.map(p => p.quantitySold) : [] }];
  
  const doughnutOptions = { 
    ...chartBaseOptions, 
    labels: data?.orderStatusCount ? Object.keys(data.orderStatusCount) : [], 
    title: { text: 'Status no Período', align: 'left', style: { fontSize: '16px', fontWeight: 500 } }, 
    legend: { position: 'bottom' },
    plotOptions: { pie: { donut: { size: '70%', labels: { show: true, total: { show: true, label: 'Total', fontSize: '16px' } } } } },
    stroke: { show: false },
    colors: [theme.palette.primary.main, theme.palette.primary.light, theme.palette.primary.dark, '#90CAF9', '#1565C0', '#0D47A1']
  };
  const doughnutSeries = data?.orderStatusCount ? Object.values(data.orderStatusCount) : [];

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" sx={{ fontWeight: 'bold' }}>Dashboard</Typography>
        <Box sx={{ display: 'flex', gap: 2 }}>
          <DatePicker 
            label="De" 
            value={dateRange.startDate} 
            onChange={(newValue) => setDateRange(prev => ({ ...prev, startDate: newValue }))} 
            renderInput={(params) => <TextField {...params} size="small" sx={{ width: 150 }} />}
          />
          <DatePicker 
            label="Até" 
            value={dateRange.endDate} 
            onChange={(newValue) => setDateRange(prev => ({ ...prev, endDate: newValue }))} 
            renderInput={(params) => <TextField {...params} size="small" sx={{ width: 150 }} />}
          />
        </Box>
      </Box>

      {loading ? <Box sx={{ display: 'flex', justifyContent: 'center', mt: 5 }}><CircularProgress /></Box> : error ? <Alert severity="error">{error}</Alert> : !data ? <Typography>Nenhum dado.</Typography> : (
        <Grid container spacing={3}>
          <Grid item xs={12} sm={6} md={3}><StatCard title="Faturamento" value={`R$ ${data.totalRevenueToday.toFixed(2)}`} icon={<MonetizationOnIcon />} series={[{ data: [0, data.totalRevenueToday] }]} /></Grid>
          <Grid item xs={12} sm={6} md={3}><StatCard title="Pedidos" value={data.totalOrdersToday} icon={<ShoppingCartIcon />} series={[{ data: [0, data.totalOrdersToday] }]} /></Grid>
          <Grid item xs={12} sm={6} md={3}><StatCard title="Ticket Médio" value={`R$ ${data.averageTicketToday.toFixed(2)}`} icon={<TrendingUpIcon />} series={[{ data: [0, data.averageTicketToday] }]} /></Grid>
          <Grid item xs={12} sm={6} md={3}><StatCard title="Novos Clientes" value={data.newClientsToday} icon={<PersonAddIcon />} series={[{ data: [0, data.newClientsToday] }]} /></Grid>

          <Grid item xs={12} md={7}><Paper elevation={3} sx={{ p: 2, height: '100%' }}><Chart options={lineOptions} series={lineSeries} type="area" height={280} /></Paper></Grid>
          <Grid item xs={12} md={5}><Paper elevation={3} sx={{ p: 2, height: '100%' }}><Chart options={barOptions} series={barSeries} type="bar" height={280} /></Paper></Grid>

          <Grid item xs={12} md={5}><Paper elevation={3} sx={{ p: 2, height: '100%' }}><Chart options={doughnutOptions} series={doughnutSeries} type="donut" height={280} /></Paper></Grid>
          <Grid item xs={12} md={7}><RecentOrders data={data.recentOrders} /></Grid>
        </Grid>
      )}
    </Box>
  );
}
