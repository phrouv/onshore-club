import { useId } from "react";
import { cn } from "../../utils/cn";

const CONTROL =
  "w-full rounded-xl border border-input bg-card px-3 py-2 text-sm text-foreground placeholder:text-muted-foreground focus-visible:outline-2 focus-visible:outline-ring";

export function Field({ label, error, hint, children }) {
  return (
    <label className="flex flex-col gap-1 text-sm font-medium text-foreground">
      {label}
      {children}
      {hint && !error && <span className="text-xs font-normal text-muted-foreground">{hint}</span>}
      {error && (
        <span role="alert" className="text-xs font-normal text-destructive">
          {error}
        </span>
      )}
    </label>
  );
}

export function Input({ className, ...props }) {
  return <input className={cn(CONTROL, "h-10", className)} {...props} />;
}

export function Textarea({ className, ...props }) {
  return <textarea className={cn(CONTROL, "min-h-20", className)} {...props} />;
}

export function Select({ className, options, placeholder, ...props }) {
  const id = useId();
  return (
    <select id={id} className={cn(CONTROL, "h-10", className)} {...props}>
      {placeholder !== undefined && <option value="">{placeholder}</option>}
      {options.map((o) => {
        const opt = typeof o === "string" ? { value: o, label: o } : o;
        return (
          <option key={opt.value} value={opt.value}>
            {opt.label}
          </option>
        );
      })}
    </select>
  );
}
