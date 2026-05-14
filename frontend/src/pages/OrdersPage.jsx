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
    const [form, setForm] = useState({ customerId: '', productIds: [], status: 'NEW' });
    const [statusFilter, setStatusFilter] = useState('');
    const [page, setPage] = useState(1);
    const [size, setSize] = useState(5);

    const loadRefs = async () => {
        const [c, p] = await Promise.all([customerService.list(), productService.list()]);
        setCustomers(c); setProducts(p);
    };
    const loadOrders = async () => { try { setOrders(await orderService.list()); } catch (e) { onError(e.message); } };
    useEffect(() => { loadRefs().catch(e => onError(e.message)); loadOrders(); }, []);

    const create = async () => {
        try { await orderService.create({ ...form, customerId: Number(form.customerId) }); onSuccess('Order created'); setForm({ customerId: '', productIds: [], status: 'NEW' }); loadOrders(); } catch (e) { onError(e.message); }
    };

    const filtered = useMemo(() => orders.filter(o => !statusFilter || o.status === statusFilter), [orders, statusFilter]);
    const totalPages = Math.max(1, Math.ceil(filtered.length / size));
    const safePage = Math.min(page, totalPages);
    const paged = filtered.slice((safePage - 1) * size, safePage * size);

    const toggleProduct = (id) => setForm(prev => ({ ...prev, productIds: prev.productIds.includes(id) ? prev.productIds.filter(v => v !== id) : [...prev.productIds, id] }));

    return <div>
        <div className="card">
            <h3>Create order</h3>
            <div className="row"><select value={form.customerId} onChange={(e) => setForm({ ...form, customerId: e.target.value })}><option value="">Customer</option>{customers.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}</select><select value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>{STATUSES.map(s => <option key={s} value={s}>{STATUS_LABELS[s]}</option>)}</select></div>
            <div className="minimal-list compact-checklist">{products.map(p => <label key={p.id} className="minimal-item compact-item"><input type="checkbox" checked={form.productIds.includes(p.id)} onChange={() => toggleProduct(p.id)} /><span>{p.name}</span></label>)}</div>
            <div className="create-wrap"><button className="btn btn-create" onClick={create}>Create</button></div>
        </div>

        <div className="filters-bar"><select value={statusFilter} onChange={(e)=>{setStatusFilter(e.target.value);setPage(1);}}><option value="">Все статусы</option>{STATUSES.map(s => <option key={s} value={s}>{STATUS_LABELS[s]}</option>)}</select><button className="btn btn-reset" onClick={()=>{setStatusFilter('');setPage(1);}}>Reset</button></div>
        <div className="pagination"><span className="page-info">Page {safePage} / {totalPages}</span><button className="btn" disabled={safePage===1} onClick={()=>setPage(safePage-1)}>Prev</button><button className="btn" disabled={safePage>=totalPages} onClick={()=>setPage(safePage+1)}>Next</button><select value={size} onChange={(e)=>{setSize(Number(e.target.value));setPage(1);}}><option value={5}>5 / page</option><option value={10}>10 / page</option><option value={20}>20 / page</option></select></div>

        <table className="table"><thead><tr><th>Status</th><th>Total</th><th>Customer</th><th>Products</th><th>Update status</th><th>Actions</th></tr></thead><tbody>
        {paged.map(o => <tr key={o.id}><td><span className={`badge status status-${String(o.status || '').toLowerCase()}`}>{STATUS_LABELS[o.status] || o.status}</span></td><td>{Number(o.totalAmount || 0).toFixed(2)}</td><td>{o.customer?.name}</td><td>{(o.products || []).map(p => <span className="badge category" key={p.id}>{p.name}</span>)}</td><td><select defaultValue={o.status} onChange={async (e) => { try { await orderService.updateStatus(o.id, e.target.value); onSuccess('Status updated'); loadOrders(); } catch (err) { onError(err.message); } }}>{STATUSES.map(s => <option key={s} value={s}>{STATUS_LABELS[s]}</option>)}</select></td><td><button className="btn btn-delete" onClick={async () => { try { await orderService.remove(o.id); onSuccess('Order deleted'); loadOrders(); } catch (e) { onError(e.message); } }}>Delete</button></td></tr>)}
        </tbody></table>
    </div>;
}