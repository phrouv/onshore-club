import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { CalendarDays, Compass, Home, User, Wallet } from "lucide-react";
import { Shell } from "./Shell";
import { fetchBalance } from "../../Redux/credit/creditSlice";

const LINKS = [
  { to: "/member", label: "Accueil", icon: Home, end: true },
  { to: "/member/explore", label: "Activités", icon: Compass },
  { to: "/member/planning", label: "Agenda", icon: CalendarDays },
  { to: "/member/credits", label: "Crédits", icon: Wallet },
  { to: "/member/profile", label: "Mon Club", icon: User },
];

export function MemberShell() {
  const dispatch = useDispatch();
  const balance = useSelector((s) => s.credit.balance);

  useEffect(() => {
    dispatch(fetchBalance());
  }, [dispatch]);

  return (
    <Shell
      title="ON SHORE CLUB"
      links={LINKS}
      variant="bottom"
      headerExtra={
        <span className="rounded-full bg-accent px-3 py-1 text-xs font-bold text-accent-foreground">
          {balance} crédits
        </span>
      }
    />
  );
}
