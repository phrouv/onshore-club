import { get, patch, post, put } from "../utils/api";

export const partnerService = {
  listPartners: (params) => get("/api/partners", params),
  myPartner: () => get("/api/partners/mine"),
  signupPartner: (body) => post("/api/partners", body),
  updatePartner: (id, body) => put(`/api/partners/${id}`, body),
  setStatus: (id, status) => patch(`/api/partners/${id}/status`, { status }),
  listActivities: (params) => get("/api/activities", params),
  createActivity: (body) => post("/api/activities", body),
  updateActivity: (id, body) => put(`/api/activities/${id}`, body),
};
