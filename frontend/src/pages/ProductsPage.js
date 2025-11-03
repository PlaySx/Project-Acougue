import { Container, Grid, Typography } from '@mui/material';
import ProductCard from '../components/products/ProductCard';

// Dados de exemplo. No futuro, isso virá da sua API!
const mockProducts = [
    { id: 1, name: 'Picanha (kg)', price: 89.90, image: 'https://www.beefpoint.com.br/wp-content/uploads/2021/08/picanha-capa.jpg' },
    { id: 2, name: 'Alcatra (kg)', price: 59.90, image: 'https://cdn.awsli.com.br/600x450/1677/1677289/produto/91993811/bife-de-alcatra-400g-4a5295c9.jpg' },
    { id: 3, name: 'Linguiça Toscana (kg)', price: 29.90, image: 'https://static.clubeextra.com.br/img/uploads/1/359/23537359.jpg' },
];

export default function ProductsPage() {
  return (
    <Container sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Nossos Produtos
      </Typography>
      <Grid container spacing={4}>
        {mockProducts.map((product) => (
          <Grid item key={product.id} xs={12} sm={6} md={4}>
            <ProductCard product={product} />
          </Grid>
        ))}
      </Grid>
    </Container>
  );
}