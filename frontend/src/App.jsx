import { useState } from 'react';
import CustomersPage from './pages/CustomersPage';
import ProductsPage from './pages/ProductsPage';
import OrdersPage from './pages/OrdersPage';
import CategoriesPage from './pages/CategoriesPage';
import ErrorBanner from './components/ErrorBanner';

const NAV_ITEMS = [
    { key: 'orders', label: '📦 Orders' },
    { key: 'products', label: '🧾 Products' },
    { key: 'customers', label: '👤 Customers' },
    { key: 'categories', label: '🏷️ Categories' }
];

export default function App() {
    const [page, setPage] = useState('orders');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const onError = (msg) => { setError(msg); setSuccess(''); };
    const onSuccess = (msg) => { setSuccess(msg); setError(''); };

    return <div className="app">
        <h1>Delivery Management Frontend</h1>
        <nav className="navbar">
            {NAV_ITEMS.map((item) => (
                <button
                    key={item.key}
                    className={`nav-btn ${page === item.key ? 'active' : ''}`}
                    onClick={() => setPage(item.key)}
                >
                    {item.label}
                </button>
            ))}
        </nav>
        <ErrorBanner error={error} success={success} />
        {page === 'orders' && <OrdersPage onError={onError} onSuccess={onSuccess} />}
        {page === 'products' && <ProductsPage onError={onError} onSuccess={onSuccess} />}
        {page === 'customers' && <CustomersPage onError={onError} onSuccess={onSuccess} />}
        {page === 'categories' && <CategoriesPage onError={onError} onSuccess={onSuccess} />}
    </div>;
}