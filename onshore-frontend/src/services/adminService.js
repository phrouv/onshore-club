import { del, get, post, put } from "../utils/api";

export const adminService = {
  dashboard: () => get("/api/admin/dashboard"),
  rules: () => get("/api/admin/usage-rules"),
  createRule: (body) => post("/api/admin/usage-rules", body),
  updateRule: (id, body) => put(`/api/admin/usage-rules/${id}`, body),
  deleteRule: (id) => del(`/api/admin/usage-rules/${id}`),
  settings: () => get("/api/admin/settings"),
  saveSettings: (body) => put("/api/admin/settings", body),
};
