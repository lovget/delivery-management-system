const { useEffect, useState } = React;

const parseError = async (response, fallbackMessage) => {
    try {
        const payload = await response.json();
        return new Error(payload.message || fallbackMessage);
    } catch {
        return new Error(fallbackMessage);
    }
};

const api = {
    get: async (url) => {
        const response = await fetch(url);
        if (!response.ok) {
            throw await parseError(response, 'Ошибка GET запроса');
        }
        return response.json();
    },
    send: async (url, method, body) => {
        const response = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: body ? JSON.stringify(body) : undefined
        });

        if (!response.ok) {
            throw await parseError(response, 'Ошибка запроса');
        }

        if (response.status === 204) {
            return null;
        }

        return response.json();
    },
    del: async (url) => {
        const response = await fetch(url, { method: 'DELETE' });
        if (!response.ok) {
            throw await parseError(response, 'Ошибка удаления');
        }
    }
};

function App() {
    const [customers, setCustomers] = useState([]);
    const [categories, setCategories] = useState([]);
    const [products, setProducts] = useState([]);
    const [orders, setOrders] = useState([]);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const [customerForm, setCustomerForm] = useState({ id: null, name: '', email: '', phone: '' });
    const [categoryForm, setCategoryForm] = useState({ id: null, name: '' });
    const [productForm, setProductForm] = useState({ name: '', price: '', categoryIds: [] });
    const [orderForm, setOrderForm] = useState({ customerId: '', productIds: [], status: 'NEW' });
    const [filter, setFilter] = useState({ status: 'NEW', amount: 0 });

    const loadAll = async () => {
        try {
            const [c, cat, p, o] = await Promise.all([
                api.get('/customers'), api.get('/categories'), api.get('/products'), api.get('/orders')
            ]);
            setCustomers(c); setCategories(cat); setProducts(p); setOrders(o);
        } catch (e) {
            setError(e.message || 'Ошибка загрузки данных');
        }
    };

    useEffect(() => { loadAll(); }, []);

    const notify = (msg) => { setSuccess(msg); setError(''); setTimeout(() => setSuccess(''), 2500); };
    const fail = (e) => setError(e.message || 'Ошибка запроса');

    const saveCustomer = async () => {
        try {
            if (customerForm.id) {
                await api.send(`/customers/${customerForm.id}`, 'PUT', customerForm);
                notify('Клиент обновлён');
            } else {
                await api.send('/customers', 'POST', customerForm);
                notify('Клиент создан');
            }
            setCustomerForm({ id: null, name: '', email: '', phone: '' });
            await loadAll();
        } catch (e) { fail(e); }
    };

    const saveCategory = async () => {
        try {
            if (categoryForm.id) {
                await api.send(`/categories/${categoryForm.id}`, 'PUT', categoryForm);
            } else {
                await api.send('/categories', 'POST', categoryForm);
            }
            notify('Категория сохранена');
            setCategoryForm({ id: null, name: '' });
            await loadAll();
        } catch (e) { fail(e); }
    };

    const saveProduct = async () => {
        try {
            await api.send('/products', 'POST', { ...productForm, price: Number(productForm.price) });
            notify('Товар создан');
            setProductForm({ name: '', price: '', categoryIds: [] });
            await loadAll();
        } catch (e) { fail(e); }
    };

    const saveOrder = async () => {
        try {
            await api.send('/orders', 'POST', { ...orderForm, customerId: Number(orderForm.customerId) });
            notify('Заказ создан');
            setOrderForm({ customerId: '', productIds: [], status: 'NEW' });
            await loadAll();
        } catch (e) { fail(e); }
    };

    const applyFilter = async () => {
        try {
            const data = await api.get(`/orders/filter?status=${filter.status}&amount=${Number(filter.amount)}`);
            setOrders(data);
            notify('Фильтр применён');
        } catch (e) { fail(e); }
    };

    const remove = async (path) => { try { await api.del(path); await loadAll(); notify('Удалено'); } catch (e) { fail(e); } };

    return <div className="container">
        <h1>ЛР7 — SPA клиент Delivery Management</h1>
        <small>OneToMany: Customer → Orders, ManyToMany: Order ↔ Products и Product ↔ Categories</small>
        {error && <div className="error">{error}</div>}
        {success && <div className="success">{success}</div>}

        <div className="grid">
            <section className="card">
                <h3>Клиенты (CRUD)</h3>
                <input placeholder="Имя" value={customerForm.name} onChange={e => setCustomerForm({ ...customerForm, name: e.target.value })} />
                <input placeholder="Email" value={customerForm.email} onChange={e => setCustomerForm({ ...customerForm, email: e.target.value })} />
                <input placeholder="Телефон" value={customerForm.phone} onChange={e => setCustomerForm({ ...customerForm, phone: e.target.value })} />
                <div className="inline"><button onClick={saveCustomer}>Сохранить</button><button className="secondary" onClick={() => setCustomerForm({ id: null, name: '', email: '', phone: '' })}>Сброс</button></div>
                <ul>{customers.map(c => <li key={c.id}>{c.name} ({c.email})
                    <div className="inline"><button className="secondary" onClick={() => setCustomerForm({ id: c.id, name: c.name, email: c.email, phone: c.phone })}>Редактировать</button><button className="danger" onClick={() => remove(`/customers/${c.id}`)}>Удалить</button></div>
                </li>)}</ul>
            </section>

            <section className="card">
                <h3>Категории (CRUD)</h3>
                <input placeholder="Название" value={categoryForm.name} onChange={e => setCategoryForm({ ...categoryForm, name: e.target.value })} />
                <button onClick={saveCategory}>Сохранить</button>
                <ul>{categories.map(c => <li key={c.id}>{c.name}<div className="inline"><button className="secondary" onClick={() => setCategoryForm({ id: c.id, name: c.name })}>Редактировать</button><button className="danger" onClick={() => remove(`/categories/${c.id}`)}>Удалить</button></div></li>)}</ul>
            </section>

            <section className="card">
                <h3>Товары (Create/Delete + ManyToMany c категориями)</h3>
                <input placeholder="Название" value={productForm.name} onChange={e => setProductForm({ ...productForm, name: e.target.value })} />
                <input type="number" placeholder="Цена" value={productForm.price} onChange={e => setProductForm({ ...productForm, price: e.target.value })} />
                <select multiple value={productForm.categoryIds} onChange={e => setProductForm({ ...productForm, categoryIds: [...e.target.selectedOptions].map(o => Number(o.value)) })}>
                    {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                </select>
                <button onClick={saveProduct}>Создать товар</button>
                <ul>{products.map(p => <li key={p.id}><b>{p.name}</b> - {p.price}
                    <div>{(p.categories || []).map(cat => <span className="badge" key={cat.id}>{cat.name}</span>)}</div>
                    <button className="danger" onClick={() => remove(`/products/${p.id}`)}>Удалить</button>
                </li>)}</ul>
            </section>

            <section className="card">
                <h3>Заказы (CRUD + фильтрация + связи)</h3>
                <select value={orderForm.customerId} onChange={e => setOrderForm({ ...orderForm, customerId: e.target.value })}>
                    <option value="">Выберите клиента</option>
                    {customers.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                </select>
                <select multiple value={orderForm.productIds} onChange={e => setOrderForm({ ...orderForm, productIds: [...e.target.selectedOptions].map(o => Number(o.value)) })}>
                    {products.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
                </select>
                <select value={orderForm.status} onChange={e => setOrderForm({ ...orderForm, status: e.target.value })}>
                    {['NEW', 'PROCESSING', 'DONE'].map(s => <option key={s} value={s}>{s}</option>)}
                </select>
                <button onClick={saveOrder}>Создать заказ</button>

                <h4>Фильтр заказов</h4>
                <div className="inline">
                    <select value={filter.status} onChange={e => setFilter({ ...filter, status: e.target.value })}>{['NEW', 'PROCESSING', 'DONE'].map(s => <option key={s} value={s}>{s}</option>)}</select>
                    <input type="number" value={filter.amount} onChange={e => setFilter({ ...filter, amount: e.target.value })} />
                </div>
                <div className="inline"><button onClick={applyFilter}>Применить фильтр</button><button className="secondary" onClick={loadAll}>Сбросить</button></div>

                <ul>{orders.map(o => <li key={o.id}>
                    <b>Заказ #{o.id}</b> — {o.status} — {o.totalAmount}
                    <div>Клиент (OneToMany): {o.customer?.name}</div>
                    <div>Товары (ManyToMany): {(o.products || []).map(p => <span className="badge" key={p.id}>{p.name}</span>)}</div>
                    <button className="danger" onClick={() => remove(`/orders/${o.id}`)}>Удалить</button>
                </li>)}</ul>
            </section>
        </div>
    </div>;
}

ReactDOM.createRoot(document.getElementById('root')).render(<App />);