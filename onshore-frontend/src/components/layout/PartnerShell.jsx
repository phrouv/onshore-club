import { CalendarClock, CalendarDays, Dumbbell, LayoutDashboard, Store, Wallet } from "lucide-react";
import { Shell } from "./Shell";

const LINKS = [
  { to: "/partner", label: "Tableau de bord", icon: LayoutDashboard, end: true },
  { to: "/partner/activites", label: "Activités", icon: Dumbbell },
  { to: "/partner/creneaux", label: "Créneaux", icon: CalendarClock },
  { to: "/partner/reservations", label: "Réservations", icon: CalendarDays },
  { to: "/partner/gains", label: "Gains", icon: Wallet },
  { to: "/partner/profil", label: "Profil", icon: Store },
];

export function PartnerShell() {
  return <Shell title="ON SHORE CLUB · Partenaire" links={LINKS} variant="side" />;
}
