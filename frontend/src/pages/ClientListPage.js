import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import apiClient from '../api/axiosConfig';
import { useAuth } from '../context/AuthContext';
import {
  Box, Container, Typography, Alert, Table, TableBody, TableCell,
  TableContainer, TableHead, TableRow, Paper, CircularProgress,
  TextField, Grid, Accordion, AccordionSummary, AccordionDetails, Button
} from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import AddIcon from '@mui/icons-material/Add';
import { format } from 'date-fns';

export default function ClientListPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [clients, setClients] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const [filters, setFilters] = useState({
    name: '',
    address: '',
    neighborhood: '',
    productName: '',
    startDate: null,
    endDate: null,
  });

  const fetchClients = useCallback(async () => {
    if (!user?.establishmentId) return;
    
    setLoading(true);
    setError('');
    
    try {
      const params = new URLSearchParams({ establishmentId: user.establishmentId });
      if (filters.name) params.append('name', filters.name);
      if (filters.address) params.append('address', filters.address);
      if (filters.neighborhood) params.append('neighborhood', filters.neighborhood);
      if (filters.productName) params.append('productName', filters.productName);
      if (filters.startDate) params.append('startDate', format(filters.startDate, 'yyyy-MM-dd'));
      if (filters.endDate) params.append('endDate', format(filters.endDate, 'yyyy-MM-dd'));

      const response = await apiClient.get(`/clients/advanced-search?${params.toString()}`);
      setClients(response.data);
    } catch (err) {
      setError('Falha ao buscar clientes.');
    } finally {
      setLoading(false);
    }
  }, [user, filters]);

  useEffect(() => {
    fetchClients();
  }, [fetchClients]);

  return (
    <Container maxWidth="lg">
      <Box sx={{ my: 4 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
          <Typography variant="h4" component="h1">Lista de Clientes</Typography>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => navigate('/clientes/novo')}
          >
            Novo Cliente
          </Button>
        </Box>

        <Accordion sx={{ mb: 3 }}>
          <AccordionSummary expandIcon={<ExpandMoreIcon />}><Typography>Filtros Avançados</Typography></AccordionSummary>
          <AccordionDetails>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}><TextField fullWidth label="Nome do Cliente" name="name" value={filters.name} onChange={(e) => setFilters(p => ({...p, name: e.target.value}))} /></Grid>
              <Grid item xs={12} sm={6}><TextField fullWidth label="Endereço" name="address" value={filters.address} onChange={(e) => setFilters(p => ({...p, address: e.target.value}))} /></Grid>
              <Grid item xs={12} sm={6}><TextField fullWidth label="Bairro" name="neighborhood" value={filters.neighborhood} onChange={(e) => setFilters(p => ({...p, neighborhood: e.target.value}))} /></Grid>
              <Grid item xs={12} sm={6}><TextField fullWidth label="Comprou o Produto..." name="productName" value={filters.productName} onChange={(e) => setFilters(p => ({...p, productName: e.target.value}))} /></Grid>
              <Grid item xs={12} sm={6}><DatePicker label="De (data da compra)" value={filters.startDate} onChange={(d) => setFilters(p => ({...p, startDate: d}))} renderInput={(params) => <TextField {...params} fullWidth />} /></Grid>
              <Grid item xs={12} sm={6}><DatePicker label="Até (data da compra)" value={filters.endDate} onChange={(d) => setFilters(p => ({...p, endDate: d}))} renderInput={(params) => <TextField {...params} fullWidth />} /></Grid>
            </Grid>
          </AccordionDetails>
        </Accordion>

        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

        {loading ? <CircularProgress /> : (
          <TableContainer component={Paper}>
            <Table>
              <TableHead><TableRow><TableCell>Nome</TableCell><TableCell>Telefone</TableCell><TableCell>Endereço</TableCell><TableCell>Bairro</TableCell></TableRow></TableHead>
              <TableBody>
                {clients.map((client) => (
                  <TableRow key={client.id}>
                    <TableCell>{client.name}</TableCell>
                    <TableCell>{client.numberPhone}</TableCell>
                    <TableCell>{client.address}</TableCell>
                    <TableCell>{client.addressNeighborhood}</TableCell>
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
