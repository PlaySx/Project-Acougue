import { Card, CardMedia, CardContent, Typography, CardActions, Button } from '@mui/material';

export default function ProductCard({ product }) {
  const { name, price, image } = product;

  const handleAddToCart = () => {
    // Lógica para adicionar ao carrinho virá aqui
    console.log(`Adicionando ${name} ao carrinho!`);
  };

  return (
    <Card sx={{ maxWidth: 345, display: 'flex', flexDirection: 'column', height: '100%' }}>
      <CardMedia
        component="img"
        height="140"
        image={image || 'https://via.placeholder.com/345x140.png?text=Carne'} // Imagem placeholder
        alt={name}
      />
      <CardContent sx={{ flexGrow: 1 }}>
        <Typography gutterBottom variant="h5" component="div">{name}</Typography>
        <Typography variant="body2" color="text.secondary">R$ {price.toFixed(2).replace('.', ',')}</Typography>
      </CardContent>
      <CardActions>
        <Button size="small" variant="contained" color="error" onClick={handleAddToCart}>Adicionar ao Carrinho</Button>
      </CardActions>
    </Card>
  );
}