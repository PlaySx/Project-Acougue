import React, { useState, useEffect } from 'react';
import apiClient from '../api/axiosConfig';
import { useAuth } from '../context/AuthContext';
import {
  Box, Container, Typography, Table, TableBody, TableCell,
  TableContainer, TableHead, TableRow, Paper, CircularProgress, Alert
} from '@mui/material';

export default function AuditPage() {
  const { user } = useAuth();
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchLogs = async () => {
      if (!user?.establishmentId) return;
      try {
        const response = await apiClient.get(`/audit?establishmentId=${user.establishmentId}`);
        setLogs(response.data);
      } catch (err) {
        setError('Falha ao carregar logs de auditoria.');
      } finally {
        setLoading(false);
      }
    };
    fetchLogs();
  }, [user]);

  if (user?.role !== 'ROLE_OWNER') {
    return <Alert severity="error">Acesso negado. Apenas proprietários podem ver esta página.</Alert>;
  }

  if (loading) return <CircularProgress />;

  return (
    <Container maxWidth="lg">
      <Box sx={{ my: 4 }}>
        <Typography variant="h4" gutterBottom>Log de Atividades</Typography>
        {error && <Alert severity="error">{error}</Alert>}
        
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Data/Hora</TableCell>
                <TableCell>Usuário</TableCell>
                <TableCell>Ação</TableCell>
                <TableCell>Entidade</TableCell>
                <TableCell>Detalhes</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {logs.map((log) => (
                <TableRow key={log.id}>
                  <TableCell>{new Date(log.timestamp).toLocaleString()}</TableCell>
                  <TableCell>{log.username}</TableCell>
                  <TableCell>{log.action}</TableCell>
                  <TableCell>{log.entityName} (ID: {log.entityId})</TableCell>
                  <TableCell>{log.details}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Box>
    </Container>
  );
}
