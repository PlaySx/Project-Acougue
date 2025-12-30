import React, { useState } from 'react';
import {
  Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField
} from '@mui/material';

export default function QuantityDialog({ open, onClose, onSubmit, product }) {
  const [value, setValue] = useState('');
  const isPerKg = product?.pricingType === 'PER_KG';
  const label = isPerKg ? "Peso em gramas (g)" : "Quantidade (unidades)";

  const handleSubmit = () => {
    const numValue = parseInt(value, 10);
    if (numValue > 0) {
      onSubmit(isPerKg ? { weightInGrams: numValue } : { quantity: numValue });
      onClose();
      setValue(''); // Limpa o campo para a próxima vez
    }
  };

  const handleClose = () => {
    setValue('');
    onClose();
  };

  return (
    <Dialog open={open} onClose={handleClose} fullWidth maxWidth="xs">
      <DialogTitle>Adicionar {product?.name}</DialogTitle>
      <DialogContent>
        <TextField 
          autoFocus 
          margin="dense" 
          label={label} 
          type="number" 
          fullWidth 
          variant="standard" 
          value={value} 
          onChange={(e) => setValue(e.target.value)} 
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose}>Cancelar</Button>
        <Button onClick={handleSubmit}>Adicionar</Button>
      </DialogActions>
    </Dialog>
  );
}
