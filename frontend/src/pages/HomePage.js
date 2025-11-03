import { Typography, Container } from '@mui/material';

export default function HomePage() {
  return (
    <Container sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Bem-vindo ao Açougue Online!
      </Typography>
      <Typography variant="body1">
        As melhores carnes, com a melhor qualidade, direto na sua casa.
      </Typography>
    </Container>
  );
}