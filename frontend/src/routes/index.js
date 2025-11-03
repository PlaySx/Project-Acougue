import{ useRoutes, link as RouterLink} from 'react-router-dom';
import HomePage from '../pages/HomePage';
 import ProductsPage from '../pages/ProductsPage';
 import CartPage from '../pages/CartPage';
 import LoginPage from '../pages/LoginPage';
 import RegisterPage from '../pages/RegisterPage';
 import NotFoundPage from '../pages/NotFoundPage';
 import Header from '../components/layout/Header';

 const AppLayout = ({ children }) => (
    <>
      <Header />
      <main>{children}</main>
    </>
  );

  export default function AppRoutes() {
    return useRoutes([
      { path: '/', element: <AppLayout children={<HomePage />} /> },
      { path: 'produtos', element: <AppLayout children={<ProductsPage />} /> },
      { path: 'carrinho', element: <AppLayout children={<CartPage />} /> },
      { path: 'login', element: <LoginPage /> },
      { path: 'register', element: <RegisterPage /> },
      { path: '*', element: <NotFoundPage /> }
    ]);
  }