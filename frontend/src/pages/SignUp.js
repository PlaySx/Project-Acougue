import * as React from 'react';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import axios from 'axios';

import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import FormLabel from '@mui/material/FormLabel';
import FormControl from '@mui/material/FormControl';
import Link from '@mui/material/Link';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import Stack from '@mui/material/Stack';
import MuiCard from '@mui/material/Card';
import { styled } from '@mui/material/styles';
import { Alert } from '@mui/material';

import { SitemarkIcon } from '../components/CustomIcons';

const Card = styled(MuiCard)(({ theme }) => ({
  display: 'flex',
  flexDirection: 'column',
  alignSelf: 'center',
  width: '100%',
  padding: theme.spacing(4),
  gap: theme.spacing(2),
  margin: 'auto',
  [theme.breakpoints.up('sm')]: {
    maxWidth: '450px',
  },
  boxShadow: 'hsla(220, 30%, 5%, 0.05) 0px 5px 15px 0px, hsla(220, 25%, 10%, 0.05) 0px 15px 35px -5px',
}));

// Estilo corrigido para permitir a rolagem
const SignUpContainer = styled(Stack)(({ theme }) => ({
  minHeight: '100dvh', // Garante que o container ocupe pelo menos a altura da tela
  padding: theme.spacing(4, 2), // Adiciona um padding vertical
  width: '100%',
}));

export default function SignUp() {
  const navigate = useNavigate();
  const [formData, setFormData] = React.useState({
    email: '',
    password: '',
    role: 'ROLE_EMPLOYEE',
    establishmentName: '',
    cnpj: '',
    establishmentAddress: '',
  });
  const [error, setError] = React.useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');

    if (!formData.email || !formData.password) {
      setError('Email e senha são obrigatórios.');
      return;
    }
    
    if (formData.role === 'ROLE_OWNER' && (!formData.establishmentName || !formData.cnpj)) {
      setError('Nome do estabelecimento e CNPJ são obrigatórios para proprietários.');
      return;
    }

    try {
      const response = await axios.post('http://localhost:8080/auth/register', formData);
      if (response.status === 201) {
        alert('Usuário cadastrado com sucesso! Faça o login para continuar.');
        navigate('/login');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Ocorreu um erro ao cadastrar.');
    }
  };

  return (
    <SignUpContainer direction="column" justifyContent="center">
      <CssBaseline enableColorScheme />
      <Card variant="outlined">
        <SitemarkIcon />
        <Typography component="h1" variant="h4" sx={{ width: '100%', fontSize: 'clamp(2rem, 10vw, 2.15rem)' }}>
          Sign up
        </Typography>
        
        {error && <Alert severity="error" sx={{ mt: 1 }}>{error}</Alert>}

        <Box component="form" onSubmit={handleSubmit} noValidate sx={{ display: 'flex', flexDirection: 'column', width: '100%', gap: 2 }}>
          <FormControl>
            <FormLabel htmlFor="email">Email</FormLabel>
            <TextField id="email" name="email" type="email" placeholder="your@email.com" autoComplete="email" autoFocus required fullWidth variant="outlined" value={formData.email} onChange={handleChange} />
          </FormControl>
          <FormControl>
            <FormLabel htmlFor="password">Password</FormLabel>
            <TextField name="password" placeholder="••••••" type="password" id="password" autoComplete="new-password" required fullWidth variant="outlined" value={formData.password} onChange={handleChange} />
          </FormControl>
          <FormControl>
            <FormLabel htmlFor="role">Eu sou um</FormLabel>
            <TextField id="role" name="role" select fullWidth variant="outlined" value={formData.role} onChange={handleChange} SelectProps={{ native: true }}>
              <option value="ROLE_EMPLOYEE">Funcionário</option>
              <option value="ROLE_OWNER">Proprietário</option>
            </TextField>
          </FormControl>

          {formData.role === 'ROLE_OWNER' && (
            <>
              <FormControl>
                <FormLabel htmlFor="establishmentName">Nome do Estabelecimento</FormLabel>
                <TextField id="establishmentName" name="establishmentName" required fullWidth variant="outlined" value={formData.establishmentName} onChange={handleChange} />
              </FormControl>
              <FormControl>
                <FormLabel htmlFor="cnpj">CNPJ</FormLabel>
                <TextField id="cnpj" name="cnpj" type="number" required fullWidth variant="outlined" value={formData.cnpj} onChange={handleChange} />
              </FormControl>
              <FormControl>
                <FormLabel htmlFor="establishmentAddress">Endereço do Estabelecimento</FormLabel>
                <TextField id="establishmentAddress" name="establishmentAddress" fullWidth variant="outlined" value={formData.establishmentAddress} onChange={handleChange} />
              </FormControl>
            </>
          )}

          <Button type="submit" fullWidth variant="contained">
            Sign up
          </Button>
          <Typography sx={{ textAlign: 'center' }}>
            Já tem uma conta?{' '}
            <Link component={RouterLink} to="/login" variant="body2" sx={{ alignSelf: 'center' }}>
              Sign in
            </Link>
          </Typography>
        </Box>
      </Card>
    </SignUpContainer>
  );
}