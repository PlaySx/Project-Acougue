import React from 'react';
import {
  Table, TableBody, TableCell, TableHead, TableRow,
  Typography, Paper, Link
} from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';

const RecentOrders = ({ data }) => {
  return (
    <Paper elevation={3} sx={{ p: 2, height: '100%' }}>
      <Typography variant="h6" gutterBottom>
        Pedidos Recentes
      </Typography>
      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell>Cliente</TableCell>
            <TableCell>Status</TableCell>
            <TableCell align="right">Valor</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {data.map((row) => (
            <TableRow key={row.id}>
              <TableCell>{row.clientName}</TableCell>
              <TableCell>{row.status}</TableCell>
              <TableCell align="right">{`R$ ${row.totalValue.toFixed(2)}`}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
      <Link component={RouterLink} to="/pedidos" sx={{ mt: 2, display: 'block' }}>
        Ver todos os pedidos
      </Link>
    </Paper>
  );
};

export default RecentOrders;
