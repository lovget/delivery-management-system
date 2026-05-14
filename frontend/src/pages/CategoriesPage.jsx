import { useEffect, useMemo, useState } from 'react';
import { categoryService } from '../api/services';

export default function CategoriesPage({ onError, onSuccess }) {
    const [items, setItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showCreate, setShowCreate] = useState(false);
    const [name, setName] = useState('');
    const [editingId, setEditingId] = useState(null);
    const [editName, setEditName] = useState('');
    const [search, setSearch] = useState('');
    const [page, setPage] = useState(1);
    const [size, setSize] = useState(5);

    const load = async () => { try { setLoading(true); setItems(await categoryService.list()); } catch (e) { onError(e.message); } finally { setLoading(false); } };
    useEffect(() => { load(); }, []);

    const create = async () => { try { await categoryService.create({ name }); setName(''); setShowCreate(false); onSuccess('Category created'); load(); } catch (e) { onError(e.message); } };
    const remove = async (id) => { if (!window.confirm('Удалить категорию?')) return; try { await categoryService.remove(id); onSuccess('Category deleted'); load(); } catch (e) { onError(e.message); } };
    const startEdit = (item) => { setEditingId(item.id); setEditName(item.name); };
    const saveEdit = async (id) => { try { await categoryService.update(id, { name: editName }); setEditingId(null); setEditName(''); onSuccess('Category updated'); load(); } catch (e) { onError(e.message); } };

    const filtered = useMemo(() => items.filter(c => c.name.toLowerCase().includes(search.toLowerCase())), [items, search]);
    const totalPages = Math.max(1, Math.ceil(filtered.length / size));
    const safePage = Math.min(page, totalPages);
    const paged = filtered.slice((safePage - 1) * size, safePage * size);

    return <div>
        <div className="section-header"><h2>Categories</h2><button className="btn btn-create" onClick={() => setShowCreate(v => !v)}>{showCreate ? 'Close' : 'Create Category'}</button></div>
        {showCreate && <div className="card"><h3>Create category</h3><input placeholder="Category name" value={name} onChange={(e) => setName(e.target.value)} /><div className="create-wrap"><button className="btn btn-create" disabled={!name.trim()} onClick={create}>Create</button></div></div>}
        <div className="filters-bar"><input className="search" placeholder="Filter categories" value={search} onChange={(e) => { setSearch(e.target.value); setPage(1); }} /><button className="btn btn-reset" onClick={() => { setSearch(''); setPage(1); }}>Reset</button></div>
        <div className="pagination"><span className="page-info">Page {safePage} / {totalPages}</span><button className="btn" disabled={safePage === 1} onClick={() => setPage(safePage - 1)}>Prev</button><button className="btn" disabled={safePage >= totalPages} onClick={() => setPage(safePage + 1)}>Next</button><select value={size} onChange={(e) => { setSize(Number(e.target.value)); setPage(1); }}><option value={5}>5 / page</option><option value={10}>10 / page</option><option value={20}>20 / page</option></select></div>
        {loading ? <div className="card empty-state">Loading categories...</div> : <table className="table"><thead><tr><th>Name</th><th>Actions</th></tr></thead><tbody>
        {paged.length === 0 ? <tr><td colSpan={2} className="empty-state">Нет данных</td></tr> : paged.map(c => <tr key={c.id}><td>{editingId === c.id ? <input value={editName} onChange={(e) => setEditName(e.target.value)} /> : c.name}</td><td className="actions">{editingId === c.id ? <button className="btn btn-edit" onClick={() => saveEdit(c.id)}>Save</button> : <button className="btn btn-edit" onClick={() => startEdit(c)}>Edit</button>} <button className="btn btn-delete" onClick={() => remove(c.id)}>Delete</button></td></tr>)}
        </tbody></table>}
    </div>;
}