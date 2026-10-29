import { cn } from "../../utils/cn";

export function Card({ className, ...props }) {
  return <div className={cn("rounded-2xl border border-border bg-card p-4 shadow-sm", className)} {...props} />;
}

export function CardTitle({ className, ...props }) {
  return <h3 className={cn("text-base font-bold text-foreground", className)} {...props} />;
}
