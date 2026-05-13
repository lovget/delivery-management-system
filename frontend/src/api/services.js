import { request } from './client';

export const customerService = {
    list: () => request('/customers'),
    create: (payload) => request('/customers', { method: 'POST', body: JSON.stringify(payload) }),
    update: (id, payload) => request(`/customers/${id}`, { method: 'PUT', body: JSON.stringify(payload) }),
    remove: (id) => request(`/customers/${id}`, { method: 'DELETE' })
};

export const productService = {
    list: () => request('/products'),
    create: (payload) => request('/products', { method: 'POST', body: JSON.stringify(payload) }),
    update: (id, payload) => request(`/products/${id}`, { method: 'PUT', body: JSON.stringify(payload) }),
    remove: (id) => request(`/products/${id}`, { method: 'DELETE' })
};

export const categoryService = {
    list: () => request('/categories'),
    create: (payload) => request('/categories', { method: 'POST', body: JSON.stringify(payload) }),
    remove: (id) => request(`/categories/${id}`, { method: 'DELETE' })
};

export const orderService = {
    list: () => request('/orders'),
    create: (payload) => request('/orders', { method: 'POST', body: JSON.stringify(payload) }),
    remove: (id) => request(`/orders/${id}`, { method: 'DELETE' }),
    updateStatus: (id, status) => request(`/orders/${id}/status?status=${status}`, { method: 'PATCH' }),
    filter: (status, amount) => request(`/orders/filter?status=${status}&amount=${amount}`),
    page: (page, size) => request(`/orders/page?page=${page}&size=${size}`)
};