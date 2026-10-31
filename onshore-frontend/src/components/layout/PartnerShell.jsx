import {
  CalendarClock,
  CalendarDays,
  Dumbbell,
  LayoutDashboard,
  Store,
  Wallet,
} from "lucide-react";
import { Shell } from "./Shell";

const LINKS = [
  {
    to: "/partner",
    label: "Tableau de bord",
    icon: LayoutDashboard,
    end: true,
  },
  { to: "/partner/activities", label: "Activités", icon: Dumbbell },
  { to: "/partner/classes", label: "Créneaux", icon: CalendarClock },
  { to: "/partner/bookings", label: "Réservations", icon: CalendarDays },
  { to: "/partner/earnings", label: "Gains", icon: Wallet },
  { to: "/partner/profile", label: "Profil", icon: Store },
];

export function PartnerShell() {
  return (
    <Shell title="ON SHORE CLUB · Partenaire" links={LINKS} variant="side" />
  );
}
