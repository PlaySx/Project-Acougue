import * as React from 'react';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import apiClient from '../api/axiosConfig';

import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Divider from '@mui/material/Divider';
import FormControl from '@mui/material/FormControl';
import FormLabel from '@mui/material/FormLabel';
import Link from '@mui/material/Link';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import MuiCard from '@mui/material/Card';
import { styled } from '@mui/material/styles';
import { GoogleIcon, FacebookIcon, SitemarkIcon } from '../components/CustomIcons';
import { Alert, MenuItem } from '@mui/material';

const Card = styled(MuiCard)(({ theme }) => ({
  display: 'flex',
  flexDirection: 'column',
  alignSelf: 'center',
  gap: theme.spacing(2),
  width: '100%',
  padding: theme.spacing(4),
  margin: 'auto',
  [theme.breakpoints.up('sm')]: {
    maxWidth: '500px',
  },
  boxShadow: 'hsla(220, 30%, 5%, 0.05) 0px 5px 15px 0px, hsla(220, 25%, 10%, 0.05) 0px 15px 35px -5px',
}));

const SignUpContainer = styled(Stack)(({ theme }) => ({
  minHeight: '100dvh',
  padding: theme.spacing(4, 2),
  width: '100%',
  justifyContent: 'center',
}));

export default function SignUp() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [error, setError] = React.useState('');
  const [role, setRole] = React.useState('ROLE_OWNER');

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    const data = new FormData(event.currentTarget);
    
    const payload = {
      role: role,
      email: data.get('email'),
      password: data.get('password'),
      establishmentName: data.get('establishmentName'),
      establishmentCnpj: data.get('cnpj'),
      establishmentId: data.get('establishmentId'),
    };

    try {
      const response = await apiClient.post('/auth/register', payload);
      if (response.data && response.data.token) {
        await login(null, response.data.token);
        navigate('/');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Erro ao cadastrar. Verifique os dados.');
    }
  };

  return (
    <SignUpContainer>
      <Card>
        <SitemarkIcon />
        <Typography component="h1" variant="h4" sx={{ width: '100%', fontSize: 'clamp(2rem, 10vw, 2.15rem)' }}>
          Crie sua conta
        </Typography>
        
        {error && <Alert severity="error" sx={{ mt: 1 }}>{error}</Alert>}

        <Box component="form" onSubmit={handleSubmit} noValidate sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
          
          <FormControl fullWidth>
            <FormLabel>Tipo de Conta</FormLabel>
            <TextField select value={role} onChange={(e) => setRole(e.target.value)} required>
              <MenuItem value="ROLE_OWNER">Sou Proprietário</MenuItem>
              <MenuItem value="ROLE_EMPLOYEE">Sou Funcionário</MenuItem>
            </TextField>
          </FormControl>

          {role === 'ROLE_OWNER' && (
            <>
              <Divider />
              <Typography variant="subtitle2">Dados do Estabelecimento</Typography>
              <FormControl>
                <FormLabel htmlFor="establishmentName">Nome do Estabelecimento</FormLabel>
                <TextField id="establishmentName" name="establishmentName" required fullWidth />
              </FormControl>
              <FormControl>
                <FormLabel htmlFor="cnpj">CNPJ</FormLabel>
                <TextField id="cnpj" name="cnpj" required fullWidth />
              </FormControl>
            </>
          )}

          {role === 'ROLE_EMPLOYEE' && (
            <>
              <Divider />
              <FormControl>
                <FormLabel htmlFor="establishmentId">ID do Estabelecimento</FormLabel>
                <TextField type="number" id="establishmentId" name="establishmentId" required fullWidth helperText="Peça o ID para o proprietário do seu estabelecimento." />
              </FormControl>
            </>
          )}

          <Divider />
          <Typography variant="subtitle2">Seus Dados de Acesso</Typography>
          <FormControl>
            <FormLabel htmlFor="email">Email</FormLabel>
            <TextField type="email" id="email" name="email" autoComplete="email" required fullWidth />
          </FormControl>
          <FormControl>
            <FormLabel htmlFor="password">Senha</FormLabel>
            <TextField type="password" id="password" name="password" required fullWidth />
          </FormControl>
          
          <Button type="submit" fullWidth variant="contained" sx={{ mt: 2 }}>
            Criar Conta
          </Button>
          
          <Link component={RouterLink} to="/login" variant="body2" sx={{ alignSelf: 'center' }}>
            Já tem uma conta? Faça login
          </Link>
        </Box>
      </Card>
    </SignUpContainer>
  );
}
