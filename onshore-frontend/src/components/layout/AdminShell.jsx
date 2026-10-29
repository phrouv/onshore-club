import { Banknote, CalendarDays, Coins, LayoutDashboard, Settings, Store, Users } from "lucide-react";
import { Shell } from "./Shell";

const LINKS = [
  { to: "/admin", label: "Tableau de bord", icon: LayoutDashboard, end: true },
  { to: "/admin/partenaires", label: "Partenaires", icon: Store },
  { to: "/admin/membres", label: "Membres", icon: Users },
  { to: "/admin/reservations", label: "Réservations", icon: CalendarDays },
  { to: "/admin/credits", label: "Crédits & formules", icon: Coins },
  { to: "/admin/paiements", label: "Paiements", icon: Banknote },
  { to: "/admin/regles", label: "Règles", icon: Settings },
];

export function AdminShell() {
  return <Shell title="ON SHORE CLUB · Admin" links={LINKS} variant="side" />;
}
