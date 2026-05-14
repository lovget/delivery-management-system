import { useEffect, useMemo, useState } from 'react';
import { customerService, orderService, productService } from '../api/services';

const STATUSES = ['NEW', 'PROCESSING', 'COOKING', 'DELIVERING', 'DONE', 'CANCELED'];

const STATUS_LABELS = {
    NEW: 'Новый',
    PROCESSING: 'Подтвержден',
    COOKING: 'Готовится',
    DELIVERING: 'В доставке',
    DONE: 'Доставлен',
    CANCELED: 'Отменен'
};

export default function OrdersPage({ onError, onSuccess }) {
    const [orders, setOrders] = useState([]);
    const [customers, setCustomers] = useState([]);
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showCreate, setShowCreate] = useState(false);
    const [productSearch, setProductSearch] = useState('');
    const [form, setForm] = useState({ customerId: '', productIds: [], status: 'NEW' });
    const [statusFilter, setStatusFilter] = useState('');
    const [page, setPage] = useState(1);
    const [size, setSize] = useState(5);

    const loadRefs = async () => {
        const [c, p] = await Promise.all([customerService.list(), productService.list()]);
        setCustomers(c); setProducts(p);
    };
    const loadOrders = async () => {
        try { setLoading(true); setOrders(await orderService.list()); } catch (e) { onError(e.message); } finally { setLoading(false); }
    };
    useEffect(() => { loadRefs().catch(e => onError(e.message)); loadOrders(); }, []);

    const create = async () => {
        try {
            await orderService.create({ ...form, customerId: Number(form.customerId) });
            onSuccess('Order created');
            setForm({ customerId: '', productIds: [], status: 'NEW' });
            setShowCreate(false);
            loadOrders();
        } catch (e) { onError(e.message); }
    };

    const filtered = useMemo(() => orders.filter(o => !statusFilter || o.status === statusFilter), [orders, statusFilter]);
    const totalPages = Math.max(1, Math.ceil(filtered.length / size));
    const safePage = Math.min(page, totalPages);
    const paged = filtered.slice((safePage - 1) * size, safePage * size);

    const selectedProducts = useMemo(() => products.filter(p => form.productIds.includes(p.id)), [products, form.productIds]);
    const searchableProducts = useMemo(() => products.filter(p => p.name.toLowerCase().includes(productSearch.toLowerCase()) && !form.productIds.includes(p.id)), [products, productSearch, form.productIds]);
    const isCreateDisabled = !form.customerId || form.productIds.length === 0;

    return <div>
        <div className="section-header">
            <h2>Orders</h2>
            <button className="btn btn-create" onClick={() => setShowCreate(v => !v)}>{showCreate ? 'Close' : 'Create Order'}</button>
        </div>

        {showCreate && <div className="card">
            <h3>Create order</h3>
            <div className="row"><select value={form.customerId} onChange={(e) => setForm({ ...form, customerId: e.target.value })}><option value="">Customer</option>{customers.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}</select><select value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>{STATUSES.map(s => <option key={s} value={s}>{STATUS_LABELS[s]}</option>)}</select></div>

            <div className="multiselect">
                <input placeholder="Search products" value={productSearch} onChange={(e) => setProductSearch(e.target.value)} />
                <div className="tags-wrap">{selectedProducts.length === 0 ? <span className="hint">Выберите товары</span> : selectedProducts.map(p => <span key={p.id} className="badge tag">{p.name}<button onClick={() => setForm(prev => ({ ...prev, productIds: prev.productIds.filter(id => id !== p.id) }))}>×</button></span>)}</div>
                <div className="dropdown-options">{searchableProducts.length === 0 ? <div className="hint">Нет подходящих товаров</div> : searchableProducts.map(p => <button key={p.id} className="option-item" onClick={() => setForm(prev => ({ ...prev, productIds: [...prev.productIds, p.id] }))}><span>{p.name}</span><span>{Number(p.price).toFixed(2)} ₽</span></button>)}</div>
            </div>

            <div className="create-wrap"><button className="btn btn-create" disabled={isCreateDisabled} onClick={create}>Create</button></div>
        </div>}

        <div className="filters-bar"><select value={statusFilter} onChange={(e)=>{setStatusFilter(e.target.value);setPage(1);}}><option value="">Все статусы</option>{STATUSES.map(s => <option key={s} value={s}>{STATUS_LABELS[s]}</option>)}</select><button className="btn btn-reset" onClick={()=>{setStatusFilter('');setPage(1);}}>Reset</button></div>
        <div className="pagination"><span className="page-info">Page {safePage} / {totalPages}</span><button className="btn" disabled={safePage===1} onClick={()=>setPage(safePage-1)}>Prev</button><button className="btn" disabled={safePage>=totalPages} onClick={()=>setPage(safePage+1)}>Next</button><select value={size} onChange={(e)=>{setSize(Number(e.target.value));setPage(1);}}><option value={5}>5 / page</option><option value={10}>10 / page</option><option value={20}>20 / page</option></select></div>

        {loading ? <div className="card empty-state">Loading orders...</div> : <div className="table-wrap"><table className="table table-orders"><colgroup><col className="status-col" /><col className="total-col" /><col className="customer-col" /><col className="products-col" /><col className="update-col" /><col className="actions-col" /></colgroup><thead><tr><th>Status</th><th>Total</th><th>Customer</th><th>Products</th><th>Update status</th><th className="actions-col">Actions</th></tr></thead><tbody>
        {paged.length === 0 ? <tr><td colSpan={6} className="empty-state">Нет данных</td></tr> : paged.map(o => <tr key={o.id}><td><span className={`badge status status-${String(o.status || '').toLowerCase()}`}>{STATUS_LABELS[o.status] || o.status}</span></td><td className="price">{Number(o.totalAmount || 0).toFixed(2)} ₽</td><td className="customer-cell">{o.customer?.name}</td><td>{(o.products || []).map(p => <span className="badge category" key={p.id}>{p.name}</span>)}</td><td><select className="compact-select" defaultValue={o.status} onChange={async (e) => { try { await orderService.updateStatus(o.id, e.target.value); onSuccess('Status updated'); loadOrders(); } catch (err) { onError(err.message); } }}>{STATUSES.map(s => <option key={s} value={s}>{STATUS_LABELS[s]}</option>)}</select></td><td className="actions-cell"><div className="actions"><button className="btn btn-delete" onClick={async () => { if (!window.confirm('Удалить заказ?')) return; try { await orderService.remove(o.id); onSuccess('Order deleted'); loadOrders(); } catch (e) { onError(e.message); } }}>Delete</button></div></td></tr>)}
        </tbody></table></div>}
    </div>;
}