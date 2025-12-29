import React, { useState } from 'react';
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Typography, Box, Alert, CircularProgress, Link as MuiLink, List, ListItem, ListItemText } from '@mui/material';
import UploadFileIcon from '@mui/icons-material/UploadFile';

export default function ImportDialog({ open, onClose, onImport, title, templatePath }) {
  const [file, setFile] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);

  const handleClose = () => {
    setFile(null);
    setError('');
    setResult(null);
    onClose();
  };

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    const validTypes = ['application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'application/vnd.ms-excel'];
    if (selectedFile && validTypes.includes(selectedFile.type)) {
      setFile(selectedFile);
      setError('');
    } else {
      setFile(null);
      setError('Por favor, selecione um arquivo Excel (.xlsx ou .xls)');
    }
  };

  const handleImport = async () => {
    if (!file) {
      setError('Nenhum arquivo selecionado.');
      return;
    }
    setLoading(true);
    setError('');
    setResult(null);
    const importResult = await onImport(file);
    setLoading(false);
    setResult(importResult);
  };

  return (
    <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm">
      <DialogTitle>{title}</DialogTitle>
      <DialogContent>
        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}><CircularProgress /></Box>
        ) : result ? (
          <Box>
            <Alert severity={result.failed > 0 ? 'warning' : 'success'}>
              Importação Concluída! <br />
              - {result.success} clientes importados com sucesso. <br />
              - {result.failed} linhas falharam.
            </Alert>
            {result.errors && result.errors.length > 0 && (
              <List dense sx={{ maxHeight: 200, overflow: 'auto', mt: 2 }}>
                {result.errors.map((err, i) => <ListItem key={i}><ListItemText primary={err} /></ListItem>)}
              </List>
            )}
          </Box>
        ) : (
          <Box>
            {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
            <Typography gutterBottom>1. Baixe o modelo da planilha para garantir o formato correto.</Typography>
            <MuiLink href={templatePath} download sx={{ mb: 2, display: 'inline-block' }}>Baixar Modelo Excel</MuiLink>
            <Typography gutterBottom>2. Selecione o arquivo Excel (.xlsx) preenchido.</Typography>
            <Button variant="outlined" component="label" startIcon={<UploadFileIcon />}>
              Selecionar Arquivo
              <input type="file" hidden accept=".xlsx, .xls" onChange={handleFileChange} />
            </Button>
            {file && <Typography sx={{ mt: 1, fontStyle: 'italic' }}>{file.name}</Typography>}
          </Box>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose}>{result ? 'Fechar' : 'Cancelar'}</Button>
        {!result && <Button onClick={handleImport} variant="contained" disabled={!file || loading}>Importar</Button>}
      </DialogActions>
    </Dialog>
  );
}
