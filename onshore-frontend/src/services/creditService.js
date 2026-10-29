import { get, post, put } from "../utils/api";

export const creditService = {
  balance: () => get("/api/credits/balance"),
  transactions: (memberId) => get("/api/credits/transactions", { memberId }),
  purchase: ({ credits, price }) => post("/api/credits/purchase", { credits, price }),
  adjust: (body) => post("/api/credits/adjust", body),
  plans: () => get("/api/plans"),
  savePlan: (body) => put("/api/plans", body),
  mySubscription: () => get("/api/subscriptions/me"),
  changePlan: (planId) => post("/api/subscriptions/change-plan", { planId }),
  cancelPlan: () => post("/api/subscriptions/cancel"),
  settings: () => get("/api/credit-settings"),
  saveSetting: (category, body) => put(`/api/credit-settings/${encodeURIComponent(category)}`, body),
};
