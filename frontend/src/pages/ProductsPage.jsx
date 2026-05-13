import { useEffect, useState } from 'react';
import { categoryService, productService } from '../api/services';

export default function ProductsPage({ onError, onSuccess }) {
    const [items, setItems] = useState([]);
    const [categories, setCategories] = useState([]);
    const [form, setForm] = useState({ name: '', price: '', categoryIds: [] });

    const load = async () => {
        try {
            const [products, cats] = await Promise.all([productService.list(), categoryService.list()]);
            setItems(products); setCategories(cats);
        } catch (e) { onError(e.message); }
    };
    useEffect(() => { load(); }, []);

    const create = async () => {
        try { await productService.create({ ...form, price: Number(form.price) }); setForm({ name: '', price: '', categoryIds: [] }); onSuccess('Product created'); load(); } catch (e) { onError(e.message); }
    };
    const remove = async (id) => {
        try { await productService.remove(id); onSuccess('Product deleted'); load(); } catch (e) { onError(e.message); }
    };

    return <div>
        <div className="card">
            <h3>Create product</h3>
            <div className="row"><input placeholder="Name" value={form.name} onChange={(e)=>setForm({...form,name:e.target.value})}/><input type="number" placeholder="Price" value={form.price} onChange={(e)=>setForm({...form,price:e.target.value})}/></div>
            <select multiple value={form.categoryIds} onChange={(e)=>setForm({...form,categoryIds:[...e.target.selectedOptions].map(o=>Number(o.value))})}>
                {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
            </select>
            <button onClick={create}>Create</button>
        </div>
        <table className="table"><thead><tr><th>ID</th><th>Name</th><th>Price</th><th>Categories</th><th>Actions</th></tr></thead><tbody>
        {items.map(p => <tr key={p.id}><td>{p.id}</td><td>{p.name}</td><td>{p.price}</td><td>{(p.categories||[]).map(c=><span className="badge" key={c.id}>{c.name}</span>)}</td><td><button onClick={()=>remove(p.id)}>Delete</button></td></tr>)}
        </tbody></table>
    </div>;
}
