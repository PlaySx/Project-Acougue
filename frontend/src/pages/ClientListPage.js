import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import apiClient from '../api/axiosConfig';
import { useAuth } from '../context/AuthContext';
import {
  Box, Container, Typography, Alert, Table, TableBody, TableCell,
  TableContainer, TableHead, TableRow, Paper, Link,
  TextField, Grid, Accordion, AccordionSummary, AccordionDetails, Button,
  IconButton, Snackbar
} from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import AddIcon from '@mui/icons-material/Add';
import SystemUpdateAltIcon from '@mui/icons-material/SystemUpdateAlt';
import EditIcon from '@mui/icons-material/Edit';
import SaveIcon from '@mui/icons-material/Save';
import CancelIcon from '@mui/icons-material/Cancel';
import { format } from 'date-fns';
import TableSkeleton from '../components/skeletons/TableSkeleton';
import ImportDialog from '../components/ImportDialog';

export default function ClientListPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [clients, setClients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [importOpen, setImportOpen] = useState(false);

  const [editRowId, setEditRowId] = useState(null);
  const [editedRowData, setEditedRowData] = useState({});

  const [filters, setFilters] = useState({
    name: '', address: '', neighborhood: '', productName: '', startDate: null, endDate: null,
  });

  const fetchClients = useCallback(async () => {
    if (!user?.establishmentId) return;
    setError('');
    try {
      const params = new URLSearchParams({ establishmentId: user.establishmentId });
      Object.entries(filters).forEach(([key, value]) => {
        if (value) {
          params.append(key, key.includes('Date') ? format(value, 'yyyy-MM-dd') : value);
        }
      });
      // CORREÇÃO: Rota corrigida de /advanced-search para /search
      const response = await apiClient.get(`/clients/search?${params.toString()}`);
      setClients(response.data);
    } catch (err) {
      setError('Falha ao buscar clientes.');
    } finally {
      setLoading(false);
    }
  }, [user, filters]);

  useEffect(() => {
    setLoading(true);
    fetchClients();
  }, [fetchClients]);

  const handleImport = async (file) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('establishmentId', user.establishmentId);
    try {
      const response = await apiClient.post('/clients/import', formData, { headers: { 'Content-Type': 'multipart/form-data' } });
      fetchClients();
      return response.data;
    } catch (err) {
      return { success: 0, failed: 1, errors: ['Erro de conexão ou servidor.'] };
    }
  };

  const handleEditClick = (client) => {
    setEditRowId(client.id);
    setEditedRowData({
      name: client.name,
      address: client.address,
      addressNeighborhood: client.addressNeighborhood,
      primaryPhoneNumber: client.phoneNumbers?.[0]?.number || ''
    });
  };

  const handleCancelClick = () => {
    setEditRowId(null);
    setEditedRowData({});
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setEditedRowData(prev => ({ ...prev, [name]: value }));
  };

  const handleSaveClick = async (client) => {
    try {
      const originalPhone = client.phoneNumbers?.[0] || {};
      const requestData = {
        name: editedRowData.name,
        address: editedRowData.address,
        addressNeighborhood: editedRowData.addressNeighborhood,
        observation: client.observation,
        phoneNumbers: [{ ...originalPhone, number: editedRowData.primaryPhoneNumber, isPrimary: true }],
        establishmentId: user.establishmentId
      };
      
      const response = await apiClient.put(`/clients/${client.id}`, requestData);
      setClients(prev => prev.map(c => (c.id === client.id ? response.data : c)));
      setSuccess('Cliente atualizado com sucesso!');
      handleCancelClick();
    } catch (err) {
      setError('Falha ao atualizar o cliente.');
    }
  };

  const tableColumns = ['Nome', 'Telefone Principal', 'Endereço', 'Bairro', 'Ações'];

  return (
    <Container maxWidth="lg">
      <Box sx={{ my: 4 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
          <Typography variant="h4" component="h1">Lista de Clientes</Typography>
          <Box>
            <Button variant="outlined" startIcon={<SystemUpdateAltIcon />} onClick={() => setImportOpen(true)} sx={{ mr: 2 }}>Importar</Button>
            <Button variant="contained" startIcon={<AddIcon />} onClick={() => navigate('/clientes/novo')}>Novo Cliente</Button>
          </Box>
        </Box>

        <Accordion sx={{ mb: 3 }}>
          <AccordionSummary expandIcon={<ExpandMoreIcon />}><Typography>Filtros Avançados</Typography></AccordionSummary>
          <AccordionDetails>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}><TextField fullWidth label="Nome" name="name" value={filters.name} onChange={(e) => setFilters(p => ({...p, name: e.target.value}))} /></Grid>
              <Grid item xs={12} sm={6}><TextField fullWidth label="Endereço" name="address" value={filters.address} onChange={(e) => setFilters(p => ({...p, address: e.target.value}))} /></Grid>
              <Grid item xs={12} sm={6}><TextField fullWidth label="Bairro" name="neighborhood" value={filters.neighborhood} onChange={(e) => setFilters(p => ({...p, neighborhood: e.target.value}))} /></Grid>
              <Grid item xs={12} sm={6}><TextField fullWidth label="Comprou o Produto..." name="productName" value={filters.productName} onChange={(e) => setFilters(p => ({...p, productName: e.target.value}))} /></Grid>
              <Grid item xs={12} sm={6}><DatePicker label="De (data da compra)" value={filters.startDate} onChange={(d) => setFilters(p => ({...p, startDate: d}))} renderInput={(params) => <TextField {...params} fullWidth />} /></Grid>
              <Grid item xs={12} sm={6}><DatePicker label="Até (data da compra)" value={filters.endDate} onChange={(d) => setFilters(p => ({...p, endDate: d}))} renderInput={(params) => <TextField {...params} fullWidth />} /></Grid>
            </Grid>
          </AccordionDetails>
        </Accordion>

        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        <Snackbar open={!!success} autoHideDuration={4000} onClose={() => setSuccess('')} message={success} />

        {loading ? <TableSkeleton columns={tableColumns} /> : (
          <TableContainer component={Paper}>
            <Table>
              <TableHead><TableRow>{tableColumns.map(col => <TableCell key={col}>{col}</TableCell>)}</TableRow></TableHead>
              <TableBody>
                {clients.map((client) => {
                  const isEditMode = editRowId === client.id;
                  const primaryPhone = client.phoneNumbers?.find(p => p.isPrimary) || client.phoneNumbers?.[0];
                  return (
                    <TableRow key={client.id} hover>
                      <TableCell>
                        <Link component={RouterLink} to={`/clientes/${client.id}`} sx={{ fontWeight: 'bold' }}>
                          {isEditMode ? editedRowData.name : client.name}
                        </Link>
                      </TableCell>
                      <TableCell>{isEditMode ? <TextField size="small" name="primaryPhoneNumber" value={editedRowData.primaryPhoneNumber} onChange={handleInputChange} /> : primaryPhone?.number}</TableCell>
                      <TableCell>{isEditMode ? <TextField size="small" name="address" value={editedRowData.address} onChange={handleInputChange} /> : client.address}</TableCell>
                      <TableCell>{isEditMode ? <TextField size="small" name="addressNeighborhood" value={editedRowData.addressNeighborhood} onChange={handleInputChange} /> : client.addressNeighborhood}</TableCell>
                      <TableCell>
                        {isEditMode ? (
                          <><IconButton onClick={() => handleSaveClick(client)} color="primary"><SaveIcon /></IconButton><IconButton onClick={handleCancelClick}><CancelIcon /></IconButton></>
                        ) : (
                          <IconButton onClick={() => handleEditClick(client)}><EditIcon /></IconButton>
                        )}
                      </TableCell>
                    </TableRow>
                  );
                })}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </Box>
      <ImportDialog open={importOpen} onClose={() => setImportOpen(false)} onImport={handleImport} title="Importar Clientes (Excel)" templatePath="/templates/clientes_template.xlsx" />
    </Container>
  );
}
