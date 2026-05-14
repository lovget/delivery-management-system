import { useEffect, useMemo, useState } from 'react';
import { customerService, orderService, productService } from '../api/services';

const STATUSES = ['NEW', 'COOKING', 'DELIVERING', 'DONE'];

export default function OrdersPage({ onError, onSuccess }) {
    const [orders, setOrders] = useState([]);
    const [customers, setCustomers] = useState([]);
    const [products, setProducts] = useState([]);
    const [form, setForm] = useState({ customerId: '', productIds: [], status: 'NEW' });
    const [statusFilter, setStatusFilter] = useState('');
    const [selectedIds, setSelectedIds] = useState([]);
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

    return <div>
        <div className="card">
            <h3>Create order</h3>
            <div className="row"><select value={form.customerId} onChange={(e) => setForm({ ...form, customerId: e.target.value })}><option value="">Customer</option>{customers.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}</select><select value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>{STATUSES.map(s => <option key={s} value={s}>{s}</option>)}</select></div>
            <select className="multi-select" multiple value={form.productIds} onChange={(e) => setForm({ ...form, productIds: [...e.target.selectedOptions].map(o => Number(o.value)) })}>{products.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}</select>
            <button className="btn btn-create standalone" onClick={create}>Create</button>
        </div>

        <div className="filters-bar"><select value={statusFilter} onChange={(e)=>{setStatusFilter(e.target.value);setPage(1);}}><option value="">All statuses</option>{STATUSES.map(s => <option key={s} value={s}>{s}</option>)}</select><button className="btn btn-reset" onClick={()=>{setStatusFilter('');setPage(1);}}>Reset</button></div>
        <div className="pagination"><span className="page-info">Page {safePage} / {totalPages}</span><button className="btn" disabled={safePage===1} onClick={()=>setPage(safePage-1)}>Prev</button><button className="btn" disabled={safePage>=totalPages} onClick={()=>setPage(safePage+1)}>Next</button><select value={size} onChange={(e)=>{setSize(Number(e.target.value));setPage(1);}}><option value={5}>5 / page</option><option value={10}>10 / page</option><option value={20}>20 / page</option></select></div>

        <table className="table"><thead><tr><th className="checkbox-cell"><input type="checkbox" checked={paged.length > 0 && selectedIds.length === paged.length} onChange={(e)=>setSelectedIds(e.target.checked ? paged.map(o => o.id) : [])} /></th><th>ID</th><th>Status</th><th>Total</th><th>Customer</th><th>Products</th><th>Actions</th></tr></thead><tbody>
        {paged.map(o => <tr key={o.id}><td className="checkbox-cell"><input type="checkbox" checked={selectedIds.includes(o.id)} onChange={() => setSelectedIds(prev => prev.includes(o.id) ? prev.filter(v => v !== o.id) : [...prev, o.id])} /></td><td>{o.id}</td><td><span className={`badge status status-${String(o.status || '').toLowerCase()}`}>{o.status}</span></td><td>{Number(o.totalAmount || 0).toFixed(2)}</td><td>{o.customer?.name}</td><td>{(o.products || []).map(p => <span className="badge category" key={p.id}>{p.name}</span>)}</td><td><button className="btn btn-delete" onClick={async () => { try { await orderService.remove(o.id); onSuccess('Order deleted'); loadOrders(); } catch (e) { onError(e.message); } }}>Delete</button></td></tr>)}
        </tbody></table>
    </div>;
}