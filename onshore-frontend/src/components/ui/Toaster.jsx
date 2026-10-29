import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { dismissToast } from "../../Redux/ui/uiSlice";
import { cn } from "../../utils/cn";

function Toast({ toast }) {
  const dispatch = useDispatch();

  useEffect(() => {
    const timer = setTimeout(() => dispatch(dismissToast(toast.id)), 4000);
    return () => clearTimeout(timer);
  }, [dispatch, toast.id]);

  return (
    <div
      role="status"
      className={cn(
        "rounded-xl px-4 py-3 text-sm font-medium shadow-lg",
        toast.type === "error" ? "bg-destructive text-destructive-foreground" : "bg-primary text-primary-foreground",
      )}
    >
      {toast.message}
    </div>
  );
}

export function Toaster() {
  const toasts = useSelector((s) => s.ui.toasts);
  return (
    <div className="fixed inset-x-0 top-3 z-50 mx-auto flex w-[min(26rem,92vw)] flex-col gap-2">
      {toasts.map((t) => (
        <Toast key={t.id} toast={t} />
      ))}
    </div>
  );
}
