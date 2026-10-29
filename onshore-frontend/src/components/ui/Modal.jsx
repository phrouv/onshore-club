import { useEffect, useRef } from "react";
import { X } from "lucide-react";

export function Modal({ open, title, subtitle, onClose, children }) {
  const ref = useRef(null);

  useEffect(() => {
    const dialog = ref.current;
    if (!dialog) return;
    if (open && !dialog.open) dialog.showModal();
    if (!open && dialog.open) dialog.close();
  }, [open]);

  return (
    <dialog
      ref={ref}
      onClose={onClose}
      className="m-auto w-[min(32rem,92vw)] rounded-2xl border border-border bg-card p-0 text-foreground backdrop:bg-black/40"
    >
      {open && (
        <div className="flex flex-col gap-4 p-5">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-lg font-bold">{title}</h2>
              {subtitle && (
                <p className="text-sm text-muted-foreground">{subtitle}</p>
              )}
            </div>
            <button
              type="button"
              aria-label="Fermer"
              onClick={onClose}
              className="rounded-lg p-1 hover:bg-muted"
            >
              <X className="size-5" />
            </button>
          </div>
          {children}
        </div>
      )}
    </dialog>
  );
}
