import { AppBar, Toolbar, Typography, Button, Box, Badge } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import { useAuth } from '../../context/AuthContext';

export default function Header() {
 const { isAuthenticated, user, logout } = useAuth();
  // No futuro, o número de itens virá do nosso CartContext
  const cartItemCount = 0;

  return (
    <AppBar position="static" sx={{ backgroundColor: '#212121' /* Um cinza escuro */ }}>
    <Toolbar>
    <Typography variant="h6" component={RouterLink} to="/" sx={{ flexGrow: 1, textDecoration: 'none', color: '#D32F2F'
            Carrinho
          </Button>
          {isAuthenticated ? (
                       <>
                         <Typography sx={{ ml: 2 }}>Olá, {user.name}!</Typography>
                         <Button color="inherit" onClick={logout} sx={{ ml: 1 }}>
                           Sair
                         </Button>
                       </>
                     ) : (
                       <Button
                         color="inherit"
                         variant="outlined"
                         component={RouterLink}
                         to="/login"
                         sx={{ ml: 2 }}
                       >Login</Button>
                     )}
        </Box>
      </Toolbar>
    </AppBar>
  );
}