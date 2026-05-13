import { useEffect, useState } from 'react';
import { categoryService } from '../api/services';

export default function CategoriesPage({ onError, onSuccess }) {
    const [items, setItems] = useState([]);
    const [name, setName] = useState('');

    const load = async () => {
        try {
            setItems(await categoryService.list());
        } catch (e) {
            onError(e.message);
        }
    };

    useEffect(() => {
        load();
    }, []);

    const create = async () => {
        try {
            await categoryService.create({ name });
            setName('');
            onSuccess('Category created');
            load();
        } catch (e) {
            onError(e.message);
        }
    };

    const remove = async (id) => {
        try {
            await categoryService.remove(id);
            onSuccess('Category deleted');
            load();
        } catch (e) {
            onError(e.message);
        }
    };

    return <div>
        <div className="card">
            <h3>Create category</h3>
            <div className="row">
                <input placeholder="Category name" value={name} onChange={(e) => setName(e.target.value)} />
                <button className="btn btn-create" onClick={create}>Create</button>
            </div>
        </div>

        <table className="table">
            <thead><tr><th>ID</th><th>Name</th><th>Actions</th></tr></thead>
            <tbody>
            {items.map(c => <tr key={c.id}><td>{c.id}</td><td>{c.name}</td><td><button className="btn btn-delete" onClick={() => remove(c.id)}>Delete</button></td></tr>)}
            </tbody>
        </table>
    </div>;
}