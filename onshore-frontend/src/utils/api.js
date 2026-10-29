const BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:5000";

let tokenGetter = () => null;

/** Wired by the store so the API layer never imports Redux directly. */
export const setTokenGetter = (getter) => {
  tokenGetter = getter;
};

export const getHeaders = () => {
  const token = tokenGetter();
  return {
    "Content-Type": "application/json",
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
};

export class ApiError extends Error {
  constructor(status, body) {
    super(body?.detail || body?.title || body?.message || `Erreur ${status}`);
    this.status = status;
    this.code = body?.code;
    this.errors = body?.errors;
  }
}

const toQuery = (params) => {
  const entries = Object.entries(params || {}).filter(([, v]) => v !== undefined && v !== null && v !== "");
  return entries.length ? `?${new URLSearchParams(entries).toString()}` : "";
};

export async function request(method, path, { body, params } = {}) {
  const response = await fetch(`${BASE_URL}${path}${toQuery(params)}`, {
    method,
    headers: getHeaders(),
    body: body === undefined ? undefined : JSON.stringify(body),
  });
  if (response.status === 204) return null;
  const text = await response.text();
  const data = text ? JSON.parse(text) : null;
  if (!response.ok) throw new ApiError(response.status, data);
  return data;
}

export const get = (path, params) => request("GET", path, { params });
export const post = (path, body, params) => request("POST", path, { body, params });
export const put = (path, body) => request("PUT", path, { body });
export const patch = (path, params) => request("PATCH", path, { params });
export const del = (path) => request("DELETE", path);
