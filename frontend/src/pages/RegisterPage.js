import { Container, Box, Typography, TextField, Button, Link as MuiLink } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';

export default function RegisterPage() {

  const handleSubmit = (event) => {
    event.preventDefault();
    // Lógica para registrar o usuário na API
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
          Criar Conta
        </Typography>
        <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 3 }}>
          <TextField
            autoComplete="given-name"
            name="fullName"
            required
            fullWidth
            id="fullName"
            label="Nome Completo"
            autoFocus
            margin="normal"
          />
          <TextField
            required
            fullWidth
            id="email"
            label="Endereço de Email"
            name="email"
            autoComplete="email"
            margin="normal"
          />
          <TextField
            required
            fullWidth
            name="password"
            label="Senha"
            type="password"
            id="password"
            autoComplete="new-password"
            margin="normal"
          />
          <Button
            type="submit"
            fullWidth
            variant="contained"
            color="error"
            sx={{ mt: 3, mb: 2 }}
          >
            Registrar
          </Button>
          <MuiLink component={RouterLink} to="/login" variant="body2">
            {"Já tem uma conta? Faça login"}
          </MuiLink>
        </Box>
      </Box>
    </Container>
  );
}