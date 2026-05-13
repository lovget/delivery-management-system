import { useEffect, useState } from 'react';
import { customerService } from '../api/services';

export default function CustomersPage({ onError, onSuccess }) {
    const [items, setItems] = useState([]);
    const [form, setForm] = useState({ name: '', email: '', phone: '' });

    const load = async () => {
        try { setItems(await customerService.list()); } catch (e) { onError(e.message); }
    };
    useEffect(() => { load(); }, []);

    const create = async () => {
        try { await customerService.create(form); setForm({ name: '', email: '', phone: '' }); onSuccess('Customer created'); load(); } catch (e) { onError(e.message); }
    };
    const remove = async (id) => {
        try { await customerService.remove(id); onSuccess('Customer deleted'); load(); } catch (e) { onError(e.message); }
    };

    return <div>
        <div className="card">
            <h3>Create customer</h3>
            <div className="row"><input placeholder="Name" value={form.name} onChange={(e)=>setForm({...form,name:e.target.value})}/><input placeholder="Email" value={form.email} onChange={(e)=>setForm({...form,email:e.target.value})}/><input placeholder="Phone" value={form.phone} onChange={(e)=>setForm({...form,phone:e.target.value})}/></div>
            <button onClick={create}>Create</button>
        </div>
        <table className="table"><thead><tr><th>ID</th><th>Name</th><th>Email</th><th>Phone</th><th>Actions</th></tr></thead><tbody>
        {items.map(c => <tr key={c.id}><td>{c.id}</td><td>{c.name}</td><td>{c.email}</td><td>{c.phone}</td><td><button onClick={()=>remove(c.id)}>Delete</button></td></tr>)}
        </tbody></table>
    </div>;
}
