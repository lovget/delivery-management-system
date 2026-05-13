import { useEffect, useState } from 'react';
import { customerService } from '../api/services';

export default function CustomersPage({ onError, onSuccess }) {
    const [items, setItems] = useState([]);
    const [form, setForm] = useState({ name: '', email: '', phone: '' });
    const [editingId, setEditingId] = useState(null);
    const [editForm, setEditForm] = useState({ name: '', email: '', phone: '' });

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
    const startEdit = (item) => {
        setEditingId(item.id);
        setEditForm({ name: item.name, email: item.email, phone: item.phone });
    };
    const saveEdit = async (id) => {
        try {
            await customerService.update(id, editForm);
            setEditingId(null);
            onSuccess('Customer updated');
            load();
        } catch (e) { onError(e.message); }
    };

    return <div>
        <div className="card">
            <h3>Create customer</h3>
            <div className="row"><input placeholder="Name" value={form.name} onChange={(e)=>setForm({...form,name:e.target.value})}/><input placeholder="Email" value={form.email} onChange={(e)=>setForm({...form,email:e.target.value})}/><input placeholder="Phone" value={form.phone} onChange={(e)=>setForm({...form,phone:e.target.value})}/></div>
            <button className="btn btn-create" onClick={create}>Create</button>
        </div>
        <table className="table"><thead><tr><th>ID</th><th>Name</th><th>Email</th><th>Phone</th><th>Actions</th></tr></thead><tbody>
        {items.map(c => <tr key={c.id}><td>{c.id}</td><td>{editingId===c.id ? <input value={editForm.name} onChange={(e)=>setEditForm({...editForm,name:e.target.value})}/> : c.name}</td><td>{editingId===c.id ? <input value={editForm.email} onChange={(e)=>setEditForm({...editForm,email:e.target.value})}/> : c.email}</td><td>{editingId===c.id ? <input value={editForm.phone} onChange={(e)=>setEditForm({...editForm,phone:e.target.value})}/> : c.phone}</td><td className="actions">{editingId===c.id ? <button className="btn btn-edit" onClick={()=>saveEdit(c.id)}>Save</button> : <button className="btn btn-edit" onClick={()=>startEdit(c)}>Edit</button>} <button className="btn btn-delete" onClick={()=>remove(c.id)}>Delete</button></td></tr>)}
        </tbody></table>
    </div>;
}