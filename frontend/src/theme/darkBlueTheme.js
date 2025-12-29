import { createTheme } from '@mui/material/styles';

const darkBlueTheme = createTheme({
  palette: {
    mode: 'dark', // Ativa o modo escuro do MUI
    primary: {
      main: '#3399FF', // Azul elétrico tecnológico
      contrastText: '#ffffff',
    },
    secondary: {
      main: '#00C853', // Um verde tecnológico para ações secundárias
    },
    background: {
      default: '#0A1929', // Azul muito profundo (quase preto)
      paper: '#001E3C',   // Azul escuro para cards e menus
    },
    text: {
      primary: '#F3F6F9',
      secondary: '#B2BAC2',
    },
    divider: 'rgba(194, 224, 255, 0.08)', // Divisores sutis
  },
  typography: {
    fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
    h4: {
      fontWeight: 600,
      letterSpacing: '0.5px',
    },
    h6: {
      fontWeight: 500,
    },
    button: {
      textTransform: 'none', // Botões com texto normal (não tudo maiúsculo) ficam mais modernos
      fontWeight: 600,
    },
  },
  shape: {
    borderRadius: 10, // Bordas mais arredondadas
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: '8px',
          boxShadow: 'none',
          '&:hover': {
            boxShadow: '0px 0px 10px rgba(51, 153, 255, 0.5)', // Efeito de brilho ao passar o mouse
          },
        },
        contained: {
          background: 'linear-gradient(45deg, #2196F3 30%, #21CBF3 90%)', // Gradiente sutil no botão principal
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          backgroundImage: 'none', // Remove gradientes padrão do MUI em modo dark
          border: '1px solid rgba(194, 224, 255, 0.08)', // Borda sutil
          boxShadow: '0px 4px 20px rgba(0, 0, 0, 0.25)', // Sombra profunda
        },
      },
    },
    MuiAppBar: {
      styleOverrides: {
        root: {
          backgroundColor: '#0A1929', // Mesma cor do fundo para parecer transparente/integrado
          backgroundImage: 'none',
          boxShadow: 'none',
          borderBottom: '1px solid rgba(194, 224, 255, 0.08)',
        },
      },
    },
    MuiDrawer: {
      styleOverrides: {
        paper: {
          backgroundColor: '#001E3C', // Menu lateral destacado
          borderRight: '1px solid rgba(194, 224, 255, 0.08)',
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-root': {
            '& fieldset': {
              borderColor: 'rgba(194, 224, 255, 0.2)',
            },
            '&:hover fieldset': {
              borderColor: '#3399FF',
            },
          },
        },
      },
    },
  },
});

export default darkBlueTheme;
