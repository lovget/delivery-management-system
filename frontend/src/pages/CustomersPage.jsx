import { useEffect, useMemo, useState } from 'react';
import { customerService } from '../api/services';

export default function CustomersPage({ onError, onSuccess }) {
    const [items, setItems] = useState([]);
    const [form, setForm] = useState({ name: '', email: '', phone: '' });
    const [search, setSearch] = useState('');
    const [selectedIds, setSelectedIds] = useState([]);
    const [page, setPage] = useState(1);
    const [size, setSize] = useState(5);

    const load = async () => { try { setItems(await customerService.list()); } catch (e) { onError(e.message); } };
    useEffect(() => { load(); }, []);

    const filtered = useMemo(() => items.filter(c => `${c.name} ${c.email} ${c.phone}`.toLowerCase().includes(search.toLowerCase())), [items, search]);
    const totalPages = Math.max(1, Math.ceil(filtered.length / size));
    const safePage = Math.min(page, totalPages);
    const paged = filtered.slice((safePage - 1) * size, safePage * size);

    const create = async () => {
        try { await customerService.create(form); setForm({ name: '', email: '', phone: '' }); onSuccess('Customer created'); load(); } catch (e) { onError(e.message); }
    };

    return <div>
        <div className="card">
            <h3>Create customer</h3>
            <div className="row"><input placeholder="Name" value={form.name} onChange={(e)=>setForm({...form,name:e.target.value})}/><input placeholder="Email" value={form.email} onChange={(e)=>setForm({...form,email:e.target.value})}/><input placeholder="Phone" value={form.phone} onChange={(e)=>setForm({...form,phone:e.target.value})}/></div>
            <button className="btn btn-create standalone" onClick={create}>Create</button>
        </div>
        <div className="filters-bar"><input className="search" placeholder="Filter customers" value={search} onChange={(e) => { setSearch(e.target.value); setPage(1); }} /><button className="btn btn-reset" onClick={() => { setSearch(''); setPage(1); }}>Reset</button></div>
        <div className="pagination"><span className="page-info">Page {safePage} / {totalPages}</span><button className="btn" disabled={safePage === 1} onClick={() => setPage(safePage - 1)}>Prev</button><button className="btn" disabled={safePage >= totalPages} onClick={() => setPage(safePage + 1)}>Next</button><select value={size} onChange={(e) => { setSize(Number(e.target.value)); setPage(1); }}><option value={5}>5 / page</option><option value={10}>10 / page</option><option value={20}>20 / page</option></select></div>
        <table className="table"><thead><tr><th className="checkbox-cell"><input type="checkbox" checked={paged.length > 0 && selectedIds.length === paged.length} onChange={(e)=>setSelectedIds(e.target.checked ? paged.map(c => c.id) : [])}/></th><th>ID</th><th>Name</th><th>Email</th><th>Phone</th></tr></thead><tbody>
        {paged.map(c => <tr key={c.id}><td className="checkbox-cell"><input type="checkbox" checked={selectedIds.includes(c.id)} onChange={() => setSelectedIds(prev => prev.includes(c.id) ? prev.filter(v => v !== c.id) : [...prev, c.id])} /></td><td>{c.id}</td><td>{c.name}</td><td>{c.email}</td><td>{c.phone}</td></tr>)}
        </tbody></table>
    </div>;
}