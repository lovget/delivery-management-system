import { useEffect, useState } from 'react';
import { customerService, orderService, productService } from '../api/services';

const STATUSES = ['NEW', 'COOKING', 'DELIVERING', 'DONE'];

export default function OrdersPage({ onError, onSuccess }) {
    const [orders, setOrders] = useState([]);
    const [customers, setCustomers] = useState([]);
    const [products, setProducts] = useState([]);
    const [form, setForm] = useState({ customerId: '', productIds: [], status: 'NEW' });
    const [filter, setFilter] = useState({ status: 'NEW', amount: 0 });
    const [page, setPage] = useState({ page: 0, size: 5, totalPages: 0 });

    const loadRefs = async () => {
        const [c, p] = await Promise.all([customerService.list(), productService.list()]);
        setCustomers(c); setProducts(p);
    };
    const loadOrders = async () => {
        try { setOrders(await orderService.list()); } catch (e) { onError(e.message); }
    };
    useEffect(() => { loadRefs().catch(e=>onError(e.message)); loadOrders(); }, []);

    const create = async () => {
        try { await orderService.create({ ...form, customerId: Number(form.customerId) }); onSuccess('Order created'); setForm({ customerId:'', productIds:[], status:'NEW' }); loadOrders(); } catch (e) { onError(e.message); }
    };
    const remove = async (id) => {
        try { await orderService.remove(id); onSuccess('Order deleted'); loadOrders(); } catch (e) { onError(e.message); }
    };
    const updateStatus = async (id, status) => {
        try { await orderService.updateStatus(id, status); onSuccess('Status updated'); loadOrders(); } catch (e) { onError(e.message); }
    };
    const applyFilter = async () => {
        try { setOrders(await orderService.filter(filter.status, Number(filter.amount))); onSuccess('Filter applied'); } catch (e) { onError(e.message); }
    };
    const loadPage = async () => {
        try {
            const res = await orderService.page(page.page, page.size);
            setOrders(res.content || []);
            setPage(prev => ({ ...prev, totalPages: res.totalPages || 0 }));
        } catch (e) { onError(e.message); }
    };

    return <div>
        <div className="card">
            <h3>Create order</h3>
            <div className="row">
                <select value={form.customerId} onChange={(e)=>setForm({...form,customerId:e.target.value})}><option value="">Customer</option>{customers.map(c=><option key={c.id} value={c.id}>{c.name}</option>)}</select>
                <select value={form.status} onChange={(e)=>setForm({...form,status:e.target.value})}>{STATUSES.map(s=><option key={s} value={s}>{s}</option>)}</select>
            </div>
            <select multiple value={form.productIds} onChange={(e)=>setForm({...form,productIds:[...e.target.selectedOptions].map(o=>Number(o.value))})}>{products.map(p=><option key={p.id} value={p.id}>{p.name}</option>)}</select>
            <button onClick={create}>Create</button>
        </div>

        <div className="card">
            <h3>Filter + Pagination</h3>
            <div className="row">
                <select value={filter.status} onChange={(e)=>setFilter({...filter,status:e.target.value})}>{STATUSES.map(s=><option key={s} value={s}>{s}</option>)}</select>
                <input type="number" value={filter.amount} onChange={(e)=>setFilter({...filter,amount:e.target.value})} />
                <button onClick={applyFilter}>Filter</button>
                <button onClick={loadOrders}>Reset</button>
            </div>
            <div className="row">
                <input type="number" value={page.page} onChange={(e)=>setPage({...page,page:Number(e.target.value)})} />
                <input type="number" value={page.size} onChange={(e)=>setPage({...page,size:Number(e.target.value)})} />
                <button onClick={loadPage}>Load page</button>
            </div>
            <small>Total pages: {page.totalPages}</small>
        </div>

        <table className="table"><thead><tr><th>ID</th><th>Status</th><th>Total</th><th>Customer</th><th>Products</th><th>Update status</th><th>Actions</th></tr></thead><tbody>
        {orders.map(o => <tr key={o.id}><td>{o.id}</td><td>{o.status}</td><td>{o.totalAmount}</td><td>{o.customer?.name} ({o.customer?.email})</td><td>{(o.products||[]).map(p=><span className="badge" key={p.id}>{p.name}</span>)}</td><td><select defaultValue={o.status} onChange={(e)=>updateStatus(o.id,e.target.value)}>{STATUSES.map(s=><option key={s} value={s}>{s}</option>)}</select></td><td><button onClick={()=>remove(o.id)}>Delete</button></td></tr>)}
        </tbody></table>
    </div>;
}
