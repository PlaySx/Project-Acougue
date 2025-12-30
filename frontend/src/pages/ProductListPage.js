import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import apiClient from '../api/axiosConfig';
import { useAuth } from '../context/AuthContext';
import {
  Box, Container, Typography, Alert, Table, TableBody, TableCell,
  TableContainer, TableHead, TableRow, Paper, CircularProgress,
  IconButton, TextField, Snackbar, MenuItem, Grid, Button
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import SaveIcon from '@mui/icons-material/Save';
import CancelIcon from '@mui/icons-material/Cancel';
import AddIcon from '@mui/icons-material/Add';
import TableSkeleton from '../components/skeletons/TableSkeleton';

const categories = ['CARNES', 'BEBIDAS', 'MERCEARIA', 'PADARIA', 'FRIOS_E_LATICINIOS', 'HORTIFRUTI', 'OUTROS'];
const pricingTypes = ['PER_KG', 'PER_UNIT'];

// Função auxiliar para formatar o número de forma inteligente
const formatNumber = (num) => {
  return parseFloat(num.toFixed(3));
};

export default function ProductListPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  
  const [searchTerm, setSearchTerm] = useState('');
  const [editRowId, setEditRowId] = useState(null);
  const [editedRowData, setEditedRowData] = useState({});

  const isOwner = user?.role === 'ROLE_OWNER';

  const fetchProducts = useCallback(async (query) => {
    if (!user?.establishmentId) return;
    try {
      setLoading(true);
      const params = new URLSearchParams({ establishmentId: user.establishmentId });
      if (query) params.append('name', query);
      
      const response = await apiClient.get(`/products?${params.toString()}`);
      setProducts(response.data);
    } catch (err) {
      setError('Falha ao carregar os produtos.');
    } finally {
      setLoading(false);
    }
  }, [user]);

  useEffect(() => {
    const delayDebounceFn = setTimeout(() => { fetchProducts(searchTerm); }, 500);
    return () => clearTimeout(delayDebounceFn);
  }, [searchTerm, fetchProducts]);

  const handleEditClick = (product) => {
    setEditRowId(product.id);
    const stockValueForEditing = product.pricingType === 'PER_KG' 
      ? formatNumber(product.stockQuantity / 1000)
      : product.stockQuantity;
    setEditedRowData({ ...product, stockQuantity: stockValueForEditing });
  };

  const handleCancelClick = () => {
    setEditRowId(null);
    setEditedRowData({});
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setEditedRowData(prev => ({ ...prev, [name]: value }));
  };

  const handleSaveClick = async (id) => {
    try {
      const stockValueToSave = editedRowData.pricingType === 'PER_KG'
        ? Math.round(parseFloat(editedRowData.stockQuantity) * 1000)
        : parseInt(editedRowData.stockQuantity, 10);

      const requestData = { 
        ...editedRowData, 
        stockQuantity: stockValueToSave,
        establishmentId: user.establishmentId 
      };
      
      const response = await apiClient.put(`/products/${id}`, requestData);
      setProducts(prev => prev.map(p => (p.id === id ? response.data : p)));
      setSuccess('Produto atualizado com sucesso!');
      handleCancelClick();
    } catch (err) {
      setError(err.response?.data?.message || 'Falha ao atualizar o produto.');
    }
  };

  const tableColumns = ['ID', 'Nome', 'Preço', 'Estoque', 'Categoria', 'Tipo', 'Ações'];

  return (
    <Container maxWidth="lg">
      <Box sx={{ my: 4 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
          <Typography variant="h4" component="h1">Gerenciamento de Produtos</Typography>
          <Button variant="contained" startIcon={<AddIcon />} onClick={() => navigate('/produtos/novo')}>Novo Produto</Button>
        </Box>

        <Paper sx={{ p: 2, mb: 3 }}>
          <TextField fullWidth label="Buscar por Nome ou ID" variant="outlined" value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} />
        </Paper>

        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        <Snackbar open={!!success} autoHideDuration={4000} onClose={() => setSuccess('')} message={success} />

        {loading ? <TableSkeleton columns={tableColumns} /> : (
          <TableContainer component={Paper}>
            <Table>
              <TableHead><TableRow>{tableColumns.map(col => <TableCell key={col}>{col}</TableCell>)}</TableRow></TableHead>
              <TableBody>
                {products.map((product) => {
                  const isEditMode = editRowId === product.id;
                  return (
                    <TableRow key={product.id}>
                      <TableCell>{product.id}</TableCell>
                      <TableCell>{isEditMode ? <TextField size="small" name="name" value={editedRowData.name} onChange={handleInputChange} /> : product.name}</TableCell>
                      <TableCell>{isEditMode ? <TextField size="small" name="unitPrice" type="number" value={editedRowData.unitPrice} onChange={handleInputChange} InputProps={{ readOnly: !isOwner }} helperText={!isOwner ? "Apenas proprietários" : ""}/> : `R$ ${product.unitPrice.toFixed(2)}`}</TableCell>
                      <TableCell>
                        {isEditMode ? (
                          <TextField size="small" name="stockQuantity" type="number" value={editedRowData.stockQuantity} onChange={handleInputChange} helperText={product.pricingType === 'PER_KG' ? 'em kg' : 'em unidades'} />
                        ) : (
                          // LÓGICA DE EXIBIÇÃO CORRIGIDA
                          product.pricingType === 'PER_KG'
                            ? `${formatNumber(product.stockQuantity / 1000)} kg`
                            : `${product.stockQuantity} un`
                        )}
                      </TableCell>
                      <TableCell>{isEditMode ? <TextField select size="small" name="category" value={editedRowData.category} onChange={handleInputChange} sx={{ minWidth: 120 }}>{categories.map(c => <MenuItem key={c} value={c}>{c}</MenuItem>)}</TextField> : product.category}</TableCell>
                      <TableCell>{isEditMode ? <TextField select size="small" name="pricingType" value={editedRowData.pricingType} onChange={handleInputChange} sx={{ minWidth: 120 }}>{pricingTypes.map(p => <MenuItem key={p} value={p}>{p === 'PER_KG' ? 'Por Quilo' : 'Por Unidade'}</MenuItem>)}</TextField> : (product.pricingType === 'PER_KG' ? 'Por Quilo' : 'Por Unidade')}</TableCell>
                      <TableCell>{isEditMode ? (<><IconButton onClick={() => handleSaveClick(product.id)} color="primary"><SaveIcon /></IconButton><IconButton onClick={handleCancelClick}><CancelIcon /></IconButton></>) : (<IconButton onClick={() => handleEditClick(product)}><EditIcon /></IconButton>)}</TableCell>
                    </TableRow>
                  );
                })}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </Box>
    </Container>
  );
}
