import { useEffect, useMemo } from "react";
import { useDispatch, useSelector } from "react-redux";
import { fetchActivities, fetchClasses, fetchPartners } from "../Redux/catalog/catalogSlice";
import { BOOKING_WINDOW_DAYS, addDaysIso, todayIso } from "../utils/format";

/** Classes of the rolling booking window joined with their activity and partner. */
export function useClassCatalog() {
  const dispatch = useDispatch();
  const { partners, activities, classes, loading, error } = useSelector((s) => s.catalog);

  useEffect(() => {
    const today = todayIso();
    dispatch(fetchPartners({}));
    dispatch(fetchActivities({ active: true }));
    dispatch(fetchClasses({ from: today, to: addDaysIso(today, BOOKING_WINDOW_DAYS) }));
  }, [dispatch]);

  const items = useMemo(() => {
    const activityById = new Map(activities.map((a) => [a.id, a]));
    const partnerById = new Map(partners.map((p) => [p.id, p]));
    return classes
      .map((klass) => ({ ...klass, activity: activityById.get(klass.activityId), partner: partnerById.get(klass.partnerId) }))
      .filter((k) => k.activity && k.partner && k.status === "OPEN");
  }, [partners, activities, classes]);

  return { items, loading, error };
}
