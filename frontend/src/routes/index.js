import { useRoutes } from 'react-router-dom';
import HomePage from '../pages/HomePage';
import ProductListPage from '../pages/ProductListPage';
import LoginPage from '../pages/LoginPage';
import SignUp from '../pages/SignUp';
import NotFoundPage from '../pages/NotFoundPage';
import ClientCreatePage from '../pages/ClientCreatePage';
import ClientListPage from '../pages/ClientListPage';
import ProductCreatePage from '../pages/ProductCreatePage';
import OrderCreatePage from '../pages/OrderCreatePage';
import OrderListPage from '../pages/OrderListPage';
import AuditPage from '../pages/AuditPage';
import SettingsPage from '../pages/SettingsPage'; // Importa a nova página

import AppLayout from '../components/layout/AppLayout';
import ProtectedRoute from '../components/ProtectedRoute';

export default function AppRoutes() {
    return useRoutes([
      { path: '/', element: (<ProtectedRoute><AppLayout><HomePage /></AppLayout></ProtectedRoute>) },
      
      { path: 'produtos', element: (<ProtectedRoute><AppLayout><ProductListPage /></AppLayout></ProtectedRoute>) },
      { path: 'produtos/novo', element: (<ProtectedRoute><AppLayout><ProductCreatePage /></AppLayout></ProtectedRoute>) },
      
      { path: 'clientes', element: (<ProtectedRoute><AppLayout><ClientListPage /></AppLayout></ProtectedRoute>) },
      { path: 'clientes/novo', element: (<ProtectedRoute><AppLayout><ClientCreatePage /></AppLayout></ProtectedRoute>) },
      
      { path: 'pedidos', element: (<ProtectedRoute><AppLayout><OrderListPage /></AppLayout></ProtectedRoute>) },
      { path: 'pedidos/novo', element: (<ProtectedRoute><AppLayout><OrderCreatePage /></AppLayout></ProtectedRoute>) },
      
      { path: 'auditoria', element: (<ProtectedRoute><AppLayout><AuditPage /></AppLayout></ProtectedRoute>) },
      { path: 'configuracoes', element: (<ProtectedRoute><AppLayout><SettingsPage /></AppLayout></ProtectedRoute>) }, // Nova rota

      { path: 'login', element: <LoginPage /> },
      { path: 'register', element: <SignUp /> },
      
      { path: '*', element: <NotFoundPage /> }
    ]);
}
