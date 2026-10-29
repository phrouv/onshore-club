import { get, patch } from "../utils/api";

export const payoutService = {
  list: (params) => get("/api/payouts", params),
  monthly: (partnerId) => get("/api/payouts/monthly", { partnerId }),
  summary: () => get("/api/payouts/summary"),
  setStatus: (id, status) => patch(`/api/payouts/${id}/status`, { status }),
};
