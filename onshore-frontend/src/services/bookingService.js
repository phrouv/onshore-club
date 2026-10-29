import { get, post } from "../utils/api";

export const bookingService = {
  book: (classId) => post("/api/bookings", { classId }),
  mine: () => get("/api/bookings/mine"),
  cancel: (id) => post(`/api/bookings/${id}/cancel`),
  checkIn: (id, code) => post(`/api/bookings/${id}/checkin`, { code }),
  list: (params) => get("/api/bookings", params),
  validate: (code) => post("/api/bookings/validate", { code }),
};
