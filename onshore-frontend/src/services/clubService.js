import { get, patch, post, put } from "../utils/api";

export const clubService = {
  listClubs: (params) => get("/api/clubs", params),
  createClub: (body) => post("/api/clubs", body),
  updateClub: (id, body) => put(`/api/clubs/${id}`, body),
  setClubOpen: (id, open) => patch(`/api/clubs/${id}/open`, { open }),
};
