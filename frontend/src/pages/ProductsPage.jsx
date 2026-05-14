import { useEffect, useMemo, useState } from 'react';
import { categoryService, productService } from '../api/services';

export default function ProductsPage({ onError, onSuccess }) {
    const [items, setItems] = useState([]);
    const [categories, setCategories] = useState([]);
    const [form, setForm] = useState({ name: '', price: '', categoryIds: [] });
    const [editingId, setEditingId] = useState(null);
    const [editForm, setEditForm] = useState({ name: '', price: '', categoryIds: [] });
    const [search, setSearch] = useState('');
    const [page, setPage] = useState(1);
    const [size, setSize] = useState(5);

    const load = async () => {
        try {
            const [products, cats] = await Promise.all([productService.list(), categoryService.list()]);
            setItems(products); setCategories(cats);
        } catch (e) { onError(e.message); }
    };
    useEffect(() => { load(); }, []);

    const filtered = useMemo(() => items.filter(p => p.name.toLowerCase().includes(search.toLowerCase())), [items, search]);
    const totalPages = Math.max(1, Math.ceil(filtered.length / size));
    const safePage = Math.min(page, totalPages);
    const paged = filtered.slice((safePage - 1) * size, safePage * size);

    const toggleCategory = (id) => setForm(prev => ({ ...prev, categoryIds: prev.categoryIds.includes(id) ? prev.categoryIds.filter(v => v !== id) : [...prev.categoryIds, id] }));
    const toggleEditCategory = (id) => setEditForm(prev => ({ ...prev, categoryIds: prev.categoryIds.includes(id) ? prev.categoryIds.filter(v => v !== id) : [...prev.categoryIds, id] }));

    const create = async () => { try { await productService.create({ ...form, price: Number(form.price) }); setForm({ name: '', price: '', categoryIds: [] }); onSuccess('Product created'); load(); } catch (e) { onError(e.message); } };
    const remove = async (id) => { try { await productService.remove(id); onSuccess('Product deleted'); load(); } catch (e) { onError(e.message); } };
    const startEdit = (item) => { setEditingId(item.id); setEditForm({ name: item.name, price: item.price, categoryIds: (item.categories || []).map(c => c.id) }); };
    const saveEdit = async (id) => { try { await productService.update(id, { ...editForm, price: Number(editForm.price) }); setEditingId(null); onSuccess('Product updated'); load(); } catch (e) { onError(e.message); } };

    return <div>
        <div className="card">
            <h3>Create product</h3>
            <div className="row"><input placeholder="Name" value={form.name} onChange={(e)=>setForm({...form,name:e.target.value})}/><input type="number" placeholder="Price" value={form.price} onChange={(e)=>setForm({...form,price:e.target.value})}/></div>
            <div className="minimal-list">{categories.map(c => <label key={c.id} className="minimal-item"><input type="checkbox" checked={form.categoryIds.includes(c.id)} onChange={() => toggleCategory(c.id)} /><span>{c.name}</span></label>)}</div>
            <div className="create-wrap"><button className="btn btn-create" onClick={create}>Create</button></div>
        </div>

        <div className="filters-bar"><input className="search" placeholder="Filter by name" value={search} onChange={(e) => { setSearch(e.target.value); setPage(1); }} /><button className="btn btn-reset" onClick={() => { setSearch(''); setPage(1); }}>Reset</button></div>
        <div className="pagination"><span className="page-info">Page {safePage} / {totalPages}</span><button className="btn" disabled={safePage === 1} onClick={() => setPage(safePage - 1)}>Prev</button><button className="btn" disabled={safePage >= totalPages} onClick={() => setPage(safePage + 1)}>Next</button><select value={size} onChange={(e) => { setSize(Number(e.target.value)); setPage(1); }}><option value={5}>5 / page</option><option value={10}>10 / page</option><option value={20}>20 / page</option></select></div>

        <table className="table"><thead><tr><th>Name</th><th>Price</th><th>Categories</th><th>Actions</th></tr></thead><tbody>
        {paged.map(p => <tr key={p.id}><td>{editingId===p.id ? <input value={editForm.name} onChange={(e)=>setEditForm({...editForm,name:e.target.value})}/> : p.name}</td><td>{editingId===p.id ? <input type="number" value={editForm.price} onChange={(e)=>setEditForm({...editForm,price:e.target.value})}/> : p.price}</td><td>{editingId===p.id ? <div className="minimal-list minimal-list-edit">{categories.map(c => <label key={c.id} className="minimal-item"><input type="checkbox" checked={editForm.categoryIds.includes(c.id)} onChange={() => toggleEditCategory(c.id)} /><span>{c.name}</span></label>)}</div> : (p.categories||[]).map(c=><span className="badge category" key={c.id}>{c.name}</span>)}</td><td className="actions">{editingId===p.id ? <button className="btn btn-edit" onClick={()=>saveEdit(p.id)}>Save</button> : <button className="btn btn-edit" onClick={()=>startEdit(p)}>Edit</button>} <button className="btn btn-delete" onClick={()=>remove(p.id)}>Delete</button></td></tr>)}
        </tbody></table>
    </div>;
}