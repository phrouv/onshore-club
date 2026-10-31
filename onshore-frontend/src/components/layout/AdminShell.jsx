import {
  Banknote,
  CalendarDays,
  Coins,
  LayoutDashboard,
  Settings,
  Store,
  Tags,
  Users,
} from "lucide-react";
import { Shell } from "./Shell";

const LINKS = [
  { to: "/admin", label: "Tableau de bord", icon: LayoutDashboard, end: true },
  { to: "/admin/partners", label: "Partenaires", icon: Store },
  { to: "/admin/members", label: "Membres", icon: Users },
  { to: "/admin/bookings", label: "Réservations", icon: CalendarDays },
  { to: "/admin/credits", label: "Crédits & formules", icon: Coins },
  { to: "/admin/payouts", label: "Paiements", icon: Banknote },
  { to: "/admin/categories", label: "Catégories", icon: Tags },
  { to: "/admin/rules", label: "Règles", icon: Settings },
];

export function AdminShell() {
  return <Shell title="ON SHORE CLUB · Admin" links={LINKS} variant="side" />;
}
