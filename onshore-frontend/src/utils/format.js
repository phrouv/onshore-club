const euros = new Intl.NumberFormat("fr-FR", { style: "currency", currency: "EUR" });

export const money = (value) => euros.format(Number(value) || 0);

export const shortTime = (time) => (time ? time.slice(0, 5).replace(":", "h") : "");

export const longDate = (iso) =>
  new Date(`${iso}T00:00:00`).toLocaleDateString("fr-FR", { weekday: "long", day: "numeric", month: "long" });

export const shortDate = (iso) =>
  new Date(`${iso}T00:00:00`).toLocaleDateString("fr-FR", { day: "numeric", month: "short" });

export const dateTime = (value) =>
  new Date(value).toLocaleString("fr-FR", { day: "numeric", month: "short", hour: "2-digit", minute: "2-digit" });

export const todayIso = () => new Date().toISOString().slice(0, 10);

export const addDaysIso = (iso, days) => {
  const d = new Date(`${iso}T00:00:00`);
  d.setDate(d.getDate() + days);
  return d.toISOString().slice(0, 10);
};

export const BOOKING_WINDOW_DAYS = 8;

export const BOOKING_STATUS_LABEL = {
  CONFIRMED: "Confirmée",
  COMPLETED: "Terminée",
  NO_SHOW: "Absent",
  CANCELLED: "Annulée",
};

export const TX_LABEL = {
  MONTHLY_SUBSCRIPTION: "Crédits d'abonnement",
  CREDIT_PURCHASE: "Achat de crédits",
  BOOKING: "Réservation",
  CANCELLATION_REFUND: "Annulation",
  ADMIN_ADJUSTMENT: "Ajustement administrateur",
  PROMOTIONAL_CREDIT: "Crédits offerts",
  CREDIT_EXPIRATION: "Expiration (report plafonné)",
};

export const PARTNER_STATUS_LABEL = {
  PENDING: "En attente",
  ACTIVE: "Actif",
  SUSPENDED: "Suspendu",
  REJECTED: "Refusé",
};

export const PAYOUT_STATUS_LABEL = { PENDING: "En attente", PAID: "Payé" };

export const CATEGORIES = ["Yoga", "Barre", "Pilates", "Fitness", "CrossFit", "Boxe", "Escalade", "Reformer", "Autre"];

export const CATEGORY_IMAGE = {
  Yoga: "yoga-editorial.jpg",
  Pilates: "pilates-editorial.jpg",
  Reformer: "pilates-editorial.jpg",
  Barre: "pilates-editorial.jpg",
  Fitness: "fitness-editorial.jpg",
  CrossFit: "fitness-editorial.jpg",
  Boxe: "boxing-editorial.jpg",
  Escalade: "climbing-editorial.jpg",
  Autre: "outdoor-editorial.jpg",
};

export const categoryImage = (category) => `/assets/${CATEGORY_IMAGE[category] || "outdoor-editorial.jpg"}`;
