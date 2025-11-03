import { Container, Box, Typography, TextField, Button, Link as MuiLink } from '@mui/material';
 import { Link as RouterLink, useNavigate } from 'react-router-dom';
 import { useAuth } from '../context/AuthContext';

export default function LoginPage() {
const navigate = useNavigate();
const { login } = useAuth();

  const handleSubmit = (event) => {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
  };

  return (
    <Container component="main" maxWidth="xs">
      <Box
        sx={{
          marginTop: 8,
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
        }}
      >
        <Typography component="h1" variant="h5">
          Login
        </Typography>
        <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 1 }}>
          <TextField
            margin="normal"
            required
            fullWidth
            id="email"
            label="Endereço de Email"
            name="email"
            autoComplete="email"
            autoFocus
          />
          <TextField
            margin="normal"
            required
            fullWidth
            name="password"
            label="Senha"
            type="password"
            id="password"
            autoComplete="current-password"
          />
          <Button
            type="submit"
            fullWidth
            variant="contained"
            color="error"
            sx={{ mt: 3, mb: 2 }}
          >
            Entrar
          </Button>
          <MuiLink component={RouterLink} to="/register" variant="body2">
            {"Não tem uma conta? Registre-se"}
          </MuiLink>
        </Box>
      </Box>
    </Container>
  );
}