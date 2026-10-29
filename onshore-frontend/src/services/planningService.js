import { del, get, post, put } from "../utils/api";

export const planningService = {
  list: (params) => get("/api/classes", params),
  create: (body) => post("/api/classes", body),
  update: (id, body) => put(`/api/classes/${id}`, body),
  close: (id) => post(`/api/classes/${id}/close`),
  reopen: (id) => post(`/api/classes/${id}/reopen`),
  remove: (id) => del(`/api/classes/${id}`),
};
