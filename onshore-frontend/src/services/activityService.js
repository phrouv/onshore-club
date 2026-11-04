import { get, patch, post, put } from "../utils/api";

export const activityService = {
  listActivities: (params) => get("/api/activities", params),
  createActivity: (body) => post("/api/activities", body),
  updateActivity: (id, body) => put(`/api/activities/${id}`, body),
  setActivityStatus: (id, status) =>
    patch(`/api/activities/${id}/status`, { status }),
};
