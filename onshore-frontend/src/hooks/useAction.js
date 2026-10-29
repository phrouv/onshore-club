import { useCallback, useState } from "react";
import { useDispatch } from "react-redux";
import { notify } from "../Redux/ui/uiSlice";

/** Runs a thunk, reports success/failure with a toast and prevents double submissions. */
export function useAction() {
  const dispatch = useDispatch();
  const [busy, setBusy] = useState(false);

  const run = useCallback(
    async (thunkAction, successMessage) => {
      setBusy(true);
      try {
        const result = await dispatch(thunkAction).unwrap();
        if (successMessage) dispatch(notify(successMessage));
        return { ok: true, result };
      } catch (error) {
        dispatch(notify(typeof error === "string" ? error : error.message, "error"));
        return { ok: false };
      } finally {
        setBusy(false);
      }
    },
    [dispatch],
  );

  return { run, busy };
}
