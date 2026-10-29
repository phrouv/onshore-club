import { Loader2 } from "lucide-react";
import { cn } from "../../utils/cn";

export function Spinner({ className }) {
  return <Loader2 aria-label="Chargement" className={cn("size-5 animate-spin text-muted-foreground", className)} />;
}

export function EmptyState({ children }) {
  return <p className="rounded-2xl border border-dashed border-border p-6 text-center text-sm text-muted-foreground">{children}</p>;
}

export function ErrorBanner({ message }) {
  if (!message) return null;
  return (
    <p role="alert" className="rounded-xl border border-destructive/30 bg-destructive/10 p-3 text-sm text-destructive">
      {message}
    </p>
  );
}

export function PageHeader({ title, subtitle, actions }) {
  return (
    <div className="mb-4 flex flex-wrap items-end justify-between gap-3">
      <div>
        <h1 className="text-2xl font-extrabold text-foreground">{title}</h1>
        {subtitle && <p className="text-sm text-muted-foreground">{subtitle}</p>}
      </div>
      {actions}
    </div>
  );
}

export function StatCard({ label, value, hint }) {
  return (
    <div className="rounded-2xl border border-border bg-card p-4">
      <p className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">{label}</p>
      <p className="mt-1 text-2xl font-extrabold text-foreground">{value}</p>
      {hint && <p className="text-xs text-muted-foreground">{hint}</p>}
    </div>
  );
}
