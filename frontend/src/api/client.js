const API_BASE = 'http://localhost:8080';

function parseError(payload) {
    if (!payload) return 'Unknown error';
    if (payload.message && payload.details) {
        return `${payload.message}\n${payload.details.join('\n')}`;
    }
    if (payload.message) return payload.message;
    if (payload.error) return payload.error;
    return JSON.stringify(payload);
}

export async function request(path, options = {}) {
    const response = await fetch(`${API_BASE}${path}`, {
        headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
        ...options
    });
    if (response.status === 204) return null;
    const body = await response.json().catch(() => null);
    if (!response.ok) {
        throw new Error(parseError(body));
    }
    return body;
}
