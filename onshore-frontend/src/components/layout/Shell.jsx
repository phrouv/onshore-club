import { NavLink, Outlet } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { LogOut } from "lucide-react";
import { logout } from "../../Redux/auth/authSlice";
import { cn } from "../../utils/cn";

/** variant="bottom": mobile-first tab bar (members). variant="side": sidebar (partners, admins). */
export function Shell({ title, links, variant, headerExtra }) {
  const dispatch = useDispatch();
  const user = useSelector((s) => s.auth.user);

  const navLink = ({ isActive }) =>
    cn(
      "flex items-center gap-2 rounded-xl px-3 py-2 text-sm font-semibold transition",
      isActive ? "bg-primary text-primary-foreground" : "text-muted-foreground hover:bg-muted",
      variant === "bottom" && "flex-1 flex-col gap-0.5 px-1 text-xs",
    );

  const header = (
    <header className="flex items-center justify-between gap-3 border-b border-border bg-card px-4 py-3">
      <span className="text-lg font-extrabold tracking-tight text-primary">{title}</span>
      <div className="flex items-center gap-3">
        {headerExtra}
        <span className="hidden text-sm text-muted-foreground sm:inline">{user?.fullName}</span>
        <button type="button" aria-label="Se déconnecter" onClick={() => dispatch(logout())} className="rounded-lg p-2 hover:bg-muted">
          <LogOut className="size-4" />
        </button>
      </div>
    </header>
  );

  const items = links.map(({ to, label, icon: Icon, end }) => (
    <NavLink key={to} to={to} end={end} className={navLink}>
      <Icon className="size-4" />
      {label}
    </NavLink>
  ));

  if (variant === "bottom") {
    return (
      <div className="mx-auto flex min-h-screen max-w-xl flex-col bg-background">
        {header}
        <main className="flex-1 p-4 pb-24">
          <Outlet />
        </main>
        <nav className="fixed inset-x-0 bottom-0 z-10 mx-auto flex max-w-xl gap-1 border-t border-border bg-card p-2">{items}</nav>
      </div>
    );
  }

  return (
    <div className="flex min-h-screen flex-col bg-background">
      {header}
      <div className="flex flex-1 flex-col md:flex-row">
        <nav className="flex gap-1 overflow-x-auto border-b border-border bg-card p-2 md:w-56 md:flex-col md:border-b-0 md:border-r">{items}</nav>
        <main className="flex-1 p-4 md:p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
