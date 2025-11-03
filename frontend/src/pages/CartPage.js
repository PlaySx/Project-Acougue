import { Typography, Container } from '@mui/material';

export default function CartPage() {
  return (
    <Container sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Meu Carrinho
      </Typography>
    </Container>
  );
}