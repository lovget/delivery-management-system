import { useEffect, useMemo, useState } from 'react';
import { categoryService, productService } from '../api/services';

export default function ProductsPage({ onError, onSuccess }) {
    const [items, setItems] = useState([]);
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showCreate, setShowCreate] = useState(false);
    const [form, setForm] = useState({ name: '', price: '', categoryIds: [] });
    const [editingId, setEditingId] = useState(null);
    const [editForm, setEditForm] = useState({ name: '', price: '', categoryIds: [] });
    const [search, setSearch] = useState('');
    const [page, setPage] = useState(1);
    const [size, setSize] = useState(5);

    const load = async () => {
        try { setLoading(true); const [products, cats] = await Promise.all([productService.list(), categoryService.list()]); setItems(products); setCategories(cats); }
        catch (e) { onError(e.message); } finally { setLoading(false); }
    };
    useEffect(() => { load(); }, []);
    const filtered = useMemo(() => items.filter(p => p.name.toLowerCase().includes(search.toLowerCase())), [items, search]);
    const totalPages = Math.max(1, Math.ceil(filtered.length / size));
    const safePage = Math.min(page, totalPages);
    const paged = filtered.slice((safePage - 1) * size, safePage * size);

    const create = async () => { try { await productService.create({ ...form, price: Number(form.price) }); setForm({ name: '', price: '', categoryIds: [] }); setShowCreate(false); onSuccess('Product created'); load(); } catch (e) { onError(e.message); } };
    const remove = async (id) => { if (!window.confirm('Удалить товар?')) return; try { await productService.remove(id); onSuccess('Product deleted'); load(); } catch (e) { onError(e.message); } };
    const startEdit = (item) => { setEditingId(item.id); setEditForm({ name: item.name, price: item.price, categoryIds: (item.categories || []).map(c => c.id) }); };
    const saveEdit = async (id) => { try { await productService.update(id, { ...editForm, price: Number(editForm.price) }); setEditingId(null); onSuccess('Product updated'); load(); } catch (e) { onError(e.message); } };

    return <div>
        <div className="section-header"><h2>Products</h2><button className="btn btn-create" onClick={() => setShowCreate(v => !v)}>{showCreate ? 'Close' : 'Create Product'}</button></div>
        {showCreate && <div className="card"><h3>Create product</h3>
            <div className="row"><input placeholder="Name" value={form.name} onChange={(e)=>setForm({...form,name:e.target.value})}/><input type="number" placeholder="Price" value={form.price} onChange={(e)=>setForm({...form,price:e.target.value})}/></div>
            <div className="row"><select value={form.categoryIds[0] || ''} onChange={(e)=>setForm({...form,categoryIds:e.target.value ? [Number(e.target.value)] : []})}><option value="">Категория</option>{categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}</select></div>
            <div className="create-wrap"><button className="btn btn-create" disabled={!form.name.trim() || !form.price} onClick={create}>Create</button></div>
        </div>}
        <div className="filters-bar"><input className="search" placeholder="Filter by name" value={search} onChange={(e) => { setSearch(e.target.value); setPage(1); }} /><button className="btn btn-reset" onClick={() => { setSearch(''); setPage(1); }}>Reset</button></div>
        <div className="pagination"><span className="page-info">Page {safePage} / {totalPages}</span><button className="btn" disabled={safePage === 1} onClick={() => setPage(safePage - 1)}>Prev</button><button className="btn" disabled={safePage >= totalPages} onClick={() => setPage(safePage + 1)}>Next</button><select value={size} onChange={(e) => { setSize(Number(e.target.value)); setPage(1); }}><option value={5}>5 / page</option><option value={10}>10 / page</option><option value={20}>20 / page</option></select></div>
        {loading ? <div className="card empty-state">Loading products...</div> : <div className="table-wrap"><table className="table table-products"><colgroup><col className="name-col" /><col className="price-col" /><col className="categories-col" /><col className="actions-col" /></colgroup><thead><tr><th>Name</th><th>Price</th><th>Categories</th><th className="actions-col">Actions</th></tr></thead><tbody>
        {paged.length === 0 ? <tr><td colSpan={4} className="empty-state">Нет данных</td></tr> : paged.map(p => <tr key={p.id}><td>{editingId===p.id ? <input value={editForm.name} onChange={(e)=>setEditForm({...editForm,name:e.target.value})}/> : p.name}</td><td>{editingId===p.id ? <input type="number" value={editForm.price} onChange={(e)=>setEditForm({...editForm,price:e.target.value})}/> : <span className="price">{Number(p.price || 0).toFixed(2)} ₽</span>}</td><td>{editingId===p.id ? <select value={editForm.categoryIds[0] || ''} onChange={(e)=>setEditForm({...editForm,categoryIds:e.target.value ? [Number(e.target.value)] : []})}><option value="">Категория</option>{categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}</select> : (p.categories||[]).map(c=><span className="badge category" key={c.id}>{c.name}</span>)}</td><td className="actions-cell"><div className="actions">{editingId===p.id ? <button className="btn btn-edit" onClick={()=>saveEdit(p.id)}>Save</button> : <button className="btn btn-edit" onClick={()=>startEdit(p)}>Edit</button>} <button className="btn btn-delete" onClick={()=>remove(p.id)}>Delete</button></div></td></tr>)}
        </tbody></table></div>}
    </div>;
}