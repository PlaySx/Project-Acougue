import { Typography, Container } from '@mui/material';

export default function NotFoundPage() {
  return (
    <Container sx={{ py: 4, textAlign: 'center' }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Erro 404 - Página Não Encontrada
      </Typography>
    </Container>
  );
}