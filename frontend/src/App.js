import AppRoutes from './routes';
import { AuthProvider } from './context/AuthContext';
import { ThemeProvider } from '@mui/material/styles';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import darkBlueTheme from './theme/darkBlueTheme';
import CssBaseline from '@mui/material/CssBaseline';

function App() {
  return (
    // Removido o GoogleOAuthProvider
    <ThemeProvider theme={darkBlueTheme}>
      <CssBaseline />
      <LocalizationProvider dateAdapter={AdapterDateFns}>
        <AuthProvider>
          <AppRoutes />
        </AuthProvider>
      </LocalizationProvider>
    </ThemeProvider>
  );
}

export default App;
