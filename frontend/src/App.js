import AppRoutes from './routes';
import { AuthProvider } from './context/AuthContext';
import { ThemeProvider } from '@mui/material/styles';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import darkBlueTheme from './theme/darkBlueTheme'; // Importa o nosso novo tema
import CssBaseline from '@mui/material/CssBaseline';

function App() {
  return (
    // O ThemeProvider agora usa o nosso tema customizado
    <ThemeProvider theme={darkBlueTheme}>
      <CssBaseline /> {/* Garante que o fundo e as cores base sejam aplicados */}
      <LocalizationProvider dateAdapter={AdapterDateFns}>
        <AuthProvider>
          <AppRoutes />
        </AuthProvider>
      </LocalizationProvider>
    </ThemeProvider>
  );
}

export default App;
