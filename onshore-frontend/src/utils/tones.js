export const bookingTone = (status) =>
  ({ CONFIRMED: "info", COMPLETED: "success", NO_SHOW: "danger", CANCELLED: "neutral" })[status] || "neutral";

export const partnerTone = (status) =>
  ({ ACTIVE: "success", PENDING: "warning", SUSPENDED: "danger", REJECTED: "danger" })[status] || "neutral";
