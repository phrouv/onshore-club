import { cn } from "../../utils/cn";

const TONES = {
  neutral: "bg-muted text-muted-foreground",
  success: "bg-emerald-100 text-emerald-800",
  warning: "bg-amber-100 text-amber-800",
  danger: "bg-red-100 text-red-800",
  info: "bg-secondary text-secondary-foreground",
};

export function Badge({ tone = "neutral", className, ...props }) {
  return <span className={cn("inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-semibold", TONES[tone], className)} {...props} />;
}
