import React from 'react';
import {
  Dialog, DialogTitle, DialogContent, DialogActions, Button, Typography,
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Box, Chip, Divider
} from '@mui/material';

export default function OrderDetailDialog({ open, onClose, order }) {
  if (!order) return null;

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="md">
      <DialogTitle>
        Detalhes do Pedido #{order.id}
        <Chip 
          label={order.status} 
          color={order.status === 'ENTREGUE' ? 'success' : order.status === 'CANCELADO' ? 'error' : 'warning'} 
          size="small" 
          sx={{ ml: 2 }} 
        />
      </DialogTitle>
      <DialogContent dividers>
        <Box sx={{ mb: 3 }}>
          <Typography variant="subtitle1" gutterBottom>Informações Gerais</Typography>
          <Typography variant="body2"><strong>Cliente:</strong> {order.clientName}</Typography>
          <Typography variant="body2"><strong>Data:</strong> {new Date(order.datahora).toLocaleString()}</Typography>
          <Typography variant="body2"><strong>Pagamento:</strong> {order.paymentMethod}</Typography>
          {order.observation && (
            <Typography variant="body2" sx={{ mt: 1, fontStyle: 'italic' }}>
              <strong>Observação:</strong> {order.observation}
            </Typography>
          )}
        </Box>

        <Divider sx={{ mb: 2 }} />

        <Typography variant="subtitle1" gutterBottom>Itens do Pedido</Typography>
        <TableContainer component={Paper} variant="outlined">
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>Produto</TableCell>
                <TableCell align="right">Quantidade/Peso</TableCell>
                <TableCell align="right">Preço Unit.</TableCell>
                <TableCell align="right">Subtotal</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {order.items && order.items.map((item, index) => (
                <TableRow key={index}>
                  <TableCell>{item.productName}</TableCell>
                  <TableCell align="right">
                    {item.weightInGrams 
                      ? `${(item.weightInGrams / 1000).toFixed(3)} kg` 
                      : `${item.quantity} un`}
                  </TableCell>
                  <TableCell align="right">
                    {/* O preço unitário pode não vir no DTO de resposta do pedido, dependendo da implementação. 
                        Se não vier, podemos calcular ou omitir. Vamos assumir que o backend manda o preço total do item. */}
                    - 
                  </TableCell>
                  <TableCell align="right">R$ {item.price.toFixed(2)}</TableCell>
                </TableRow>
              ))}
              <TableRow>
                <TableCell colSpan={3} align="right" sx={{ fontWeight: 'bold' }}>Total</TableCell>
                <TableCell align="right" sx={{ fontWeight: 'bold' }}>R$ {order.totalValue.toFixed(2)}</TableCell>
              </TableRow>
            </TableBody>
          </Table>
        </TableContainer>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} variant="contained">Fechar</Button>
      </DialogActions>
    </Dialog>
  );
}
