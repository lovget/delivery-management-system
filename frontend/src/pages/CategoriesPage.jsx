import { useEffect, useMemo, useState } from 'react';
import { categoryService } from '../api/services';

export default function CategoriesPage({ onError, onSuccess }) {
    const [items, setItems] = useState([]);
    const [name, setName] = useState('');
    const [search, setSearch] = useState('');
    const [page, setPage] = useState(1);
    const [size, setSize] = useState(5);

    const load = async () => { try { setItems(await categoryService.list()); } catch (e) { onError(e.message); } };
    useEffect(() => { load(); }, []);

    const create = async () => { try { await categoryService.create({ name }); setName(''); onSuccess('Category created'); load(); } catch (e) { onError(e.message); } };
    const remove = async (id) => { try { await categoryService.remove(id); onSuccess('Category deleted'); load(); } catch (e) { onError(e.message); } };

    const filtered = useMemo(() => items.filter(c => c.name.toLowerCase().includes(search.toLowerCase())), [items, search]);
    const totalPages = Math.max(1, Math.ceil(filtered.length / size));
    const safePage = Math.min(page, totalPages);
    const paged = filtered.slice((safePage - 1) * size, safePage * size);

    return <div>
        <div className="card"><h3>Create category</h3><input placeholder="Category name" value={name} onChange={(e) => setName(e.target.value)} /><button className="btn btn-create standalone" onClick={create}>Create</button></div>
        <div className="filters-bar"><input className="search" placeholder="Filter categories" value={search} onChange={(e) => { setSearch(e.target.value); setPage(1); }} /><button className="btn btn-reset" onClick={() => { setSearch(''); setPage(1); }}>Reset</button></div>
        <div className="pagination"><span className="page-info">Page {safePage} / {totalPages}</span><button className="btn" disabled={safePage === 1} onClick={() => setPage(safePage - 1)}>Prev</button><button className="btn" disabled={safePage >= totalPages} onClick={() => setPage(safePage + 1)}>Next</button><select value={size} onChange={(e) => { setSize(Number(e.target.value)); setPage(1); }}><option value={5}>5 / page</option><option value={10}>10 / page</option><option value={20}>20 / page</option></select></div>
        <table className="table"><thead><tr><th>Name</th><th>Actions</th></tr></thead><tbody>
        {paged.map(c => <tr key={c.id}><td>{c.name}</td><td><button className="btn btn-delete" onClick={() => remove(c.id)}>Delete</button></td></tr>)}
        </tbody></table>
    </div>;
}